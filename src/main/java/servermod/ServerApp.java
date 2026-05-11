package servermod;

import enviroment.commands.*;
import enviroment.Request;
import enviroment.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);
    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        // инициализация менеджеров
        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(System.getenv("WORKER_CSV"));
        collectionManager.getCollection().putAll(fileManager.load());
        collectionManager.updateMaxId();

        // хук для сохранения при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                fileManager.save(collectionManager.getCollection());
                logger.info("Коллекция сохранена перед выходом.");
            } catch (IOException e) {
                logger.error("Ошибка сохранения: {}", e.getMessage());
            }
        }));

        CommandHandler handler = new CommandHandler(collectionManager);

        // Неблокирующий канал
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(PORT));
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            logger.info("Сервер запущен на порту {}", PORT);

            ByteBuffer buffer = ByteBuffer.allocate(65507); // макс размер UDP датаграммы

            while (true) {
                selector.select(); // ожидание событий
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isReadable()) {
                        DatagramChannel ch = (DatagramChannel) key.channel();
                        buffer.clear();
                        SocketAddress clientAddr = ch.receive(buffer);
                        if (clientAddr != null) {
                            buffer.flip();
                            try (ObjectInputStream ois = new ObjectInputStream(
                                    new ByteArrayInputStream(buffer.array(), 0, buffer.limit()))) {
                                Request request = (Request) ois.readObject();
                                logger.info("Получена команда: {} от {}", request.getCommand().getName(), clientAddr);

                                // обработка
                                Response response = handler.handle(request.getCommand());

                                // отправка ответа
                                ByteArrayOutputStream baosResp = new ByteArrayOutputStream();
                                try (ObjectOutputStream oos = new ObjectOutputStream(baosResp)) {
                                    oos.writeObject(response);
                                    oos.flush();
                                }
                                ByteBuffer respBuf = ByteBuffer.wrap(baosResp.toByteArray());
                                ch.send(respBuf, clientAddr);
                                logger.info("Ответ отправлен клиенту {}", clientAddr);
                            } catch (Exception e) {
                                logger.error("Ошибка обработки запроса: {}", e.getMessage());
                                // отправить сообщение об ошибке
                                Response errResp = new Response("Ошибка: " + e.getMessage());
                                ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
                                try (ObjectOutputStream oos = new ObjectOutputStream(baosErr)) {
                                    oos.writeObject(errResp);
                                }
                                ch.send(ByteBuffer.wrap(baosErr.toByteArray()), clientAddr);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Критическая ошибка сервера: {}", e.getMessage());
        }
    }
}