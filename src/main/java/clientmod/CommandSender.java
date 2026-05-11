package clientmod;

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

/**
 * Отправляет команды на сервер и получает ответы.
 * Использует UDP, неблокирующий режим, таймауты и повторные попытки.
 */
public class CommandSender {
    private final InetSocketAddress serverAddress;
    private final int timeoutMs;
    private final int maxRetries;

    public CommandSender(String host, int port, int timeoutMs, int maxRetries) {
        this.serverAddress = new InetSocketAddress(host, port);
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
    }

    /**
     * Отправляет команду на сервер и возвращает ответ.
     * @param request запрос с командой
     * @return ответ сервера
     * @throws IOException если ошибка ввода-вывода или сервер недоступен
     */
    public Response send(Request request) throws IOException, ClassNotFoundException {
        byte[] requestData = serialize(request);
        ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_WRITE); // сначала отправка

            for (int attempt = 0; attempt < maxRetries; attempt++) {
                // Отправка
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isWritable()) {
                        channel.send(sendBuffer, serverAddress);
                        sendBuffer.rewind(); // подготовить для возможной повторной отправки
                        // переключиться на чтение
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                }

                // Ожидание ответа с таймаутом
                long startTime = System.currentTimeMillis();
                ByteBuffer recvBuffer = ByteBuffer.allocate(65507);
                SocketAddress from = null;
                while (System.currentTimeMillis() - startTime < timeoutMs) {
                    if (selector.select(timeoutMs) > 0) {
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            it.remove();
                            if (key.isReadable()) {
                                from = ((DatagramChannel) key.channel()).receive(recvBuffer);
                                if (from != null && from.equals(serverAddress)) {
                                    recvBuffer.flip();
                                    Response response = deserialize(recvBuffer);
                                    return response;
                                }
                                recvBuffer.clear();
                            }
                        }
                    }
                }
                // Если ответа нет, пробуем снова
                if (attempt < maxRetries - 1) {
                    System.out.println("Таймаут, повторная отправка (" + (attempt + 1) + "/" + maxRetries + ")...");
                    // перерегистрируем на запись для повторной отправки
                    channel.register(selector, SelectionKey.OP_WRITE);
                } else {
                    throw new IOException("Сервер не ответил после " + maxRetries + " попыток");
                }
            }
        }
        throw new IOException("Не удалось получить ответ от сервера");
    }

    private byte[] serialize(Request request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            oos.flush();
        }
        return baos.toByteArray();
    }

    private Response deserialize(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.array(), 0, buffer.limit()))) {
            return (Response) ois.readObject();
        }
    }
}
