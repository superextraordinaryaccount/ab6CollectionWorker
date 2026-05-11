package clientmod;

import enviroment.Request;
import enviroment.Response;
import enviroment.commands.*;
import enviroment.workerclass.Worker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

public class ClientApp {
    private final CommandSender sender;   // отвечает за отправку на сервер
    private final InputManager inputManager;

    public ClientApp(CommandSender sender, InputManager inputManager) {
        this.sender = sender;
        this.inputManager = inputManager;
    }

    public static void main(String[] args) {
        // инициализация сканера, сендера, диспетчера
        Scanner consoleScanner = new Scanner(System.in);

        // 2. Создаём InputManager (в него передаём тот же Scanner)
        InputManager inputManager = new InputManager(consoleScanner);

        // 3. Создаём CommandSender (адрес сервера, порт, таймаут, повторы)
        CommandSender sender = new CommandSender("localhost", 8888, 3000, 3);

        // 4. Создаём CommandDispatcher (передаём sender, inputManager и consoleScanner)
        CommandDisp dispatcher = new CommandDisp(sender, inputManager,consoleScanner);

        while (true) {
            System.out.print("> ");
            String line = consoleScanner.nextLine();
            if (line.trim().isEmpty()) continue;
            dispatcher.dispatch(line);  // только вызов диспетчера
        }
    }
}

