package clientmod;


import enviroment.commands.*;


import java.io.*;

import java.util.Scanner;

public class ClientApp {


    public ClientApp() {
    }

    public static void main(String[] args) {
        // инициализация сканера, сендера, диспетчера
        Scanner consoleScanner = new Scanner(System.in);

        // 2. Создаём InputManager (в него передаём тот же Scanner)
        InputManager inputManager = new InputManager(consoleScanner);

        // 3. Создаём CommandSender (адрес сервера, порт, таймаут, повторы)
        CommandSender sender = new CommandSender("localhost", 8888, 3000, 3);

        // 4. Создаём CommandDispatcher (передаём sender, inputManager и consoleScanner)
        CommandDisp dispatcher = new CommandDisp(sender, inputManager);

        while (true) {
            System.out.print("> ");
            String line = consoleScanner.nextLine();
            if (line.trim().isEmpty()) continue;
            dispatcher.dispatch(line);  // только вызов диспетчера
        }
    }
}

