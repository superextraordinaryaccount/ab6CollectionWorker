package clientmod;

import enviroment.Request;
import enviroment.Response;
import enviroment.commands.*;
import enviroment.workerclass.Person;
import enviroment.workerclass.Worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/**
 * Диспетчер команд клиента.
 * Преобразует строки в объекты Command, отправляет их на сервер и выводит результат.
 */
public class CommandDisp {
    private final CommandSender sender;
    private final InputManager inputManager;
    private final Scanner consoleScanner;   // нужен для чтения составных объектов
    private int scriptDepth = 0;
    private static final int MAX_SCRIPT_DEPTH = 10;
    private Deque<String> history = new ArrayDeque<>(12);

    public CommandDisp(CommandSender sender, InputManager inputManager, Scanner consoleScanner) {
        this.sender = sender;
        this.inputManager = inputManager;
        this.consoleScanner = consoleScanner;
    }

    private void addToHistory(String cmdName) {
        history.addFirst(cmdName);
        if (history.size() > 12) history.removeLast();
    }

    /**
     * Основной метод диспетчеризации.
     * @param line строка команды (например, "add" или "update 123")
     */
    public void dispatch(String line) {
        String[] parts = line.trim().split("\\s+", 2);
        String cmdName = parts[0].toLowerCase();
        addToHistory(cmdName);
        String arg = parts.length > 1 ? parts[1] : null;

        try {
            switch (cmdName) {
                case "help":
                    sendCommand(new HelpCmd());
                    break;
                case "info":
                    sendCommand(new InfoCmd());
                    break;
                case "show":
                    sendCommand(new ShowCmd());
                    break;
                case "insert":
                    String key = (arg != null) ? arg : inputManager.readString("Введите ключ: ", false);
                    Worker worker = inputManager.readWorker();
                    sendCommand(new InsertCmd(worker,key));
                    break;
                case "update":
                    if (arg == null) throw new IllegalArgumentException("Требуется id");
                    Worker updatedWorker = inputManager.readWorker();
                    sendCommand(new UpdateCmd(arg, updatedWorker));
                    break;
                case "remove_key":
                    if (arg == null) throw new IllegalArgumentException("Требуется ключ");
                    sendCommand(new RemoveKeyCmd(arg));
                    break;
                case "clear":
                    sendCommand(new ClearCmd());
                    break;
                case "remove_greater":
                    Worker greaterThreshold = inputManager.readWorker();
                    sendCommand(new RemoveGreaterCmd(greaterThreshold));
                    break;
                case "remove_lower":
                    Worker lowerThreshold = inputManager.readWorker();
                    sendCommand(new RemoveLowerCmd(lowerThreshold));
                    break;
                case "max_by_end_date":
                    sendCommand(new MaxByEndDateCmd());
                    break;
                case "count_by_person":
                    Person person = inputManager.readPerson();
                    sendCommand(new CountByPersonCmd(person));
                    break;
                case "filter_less_than_end_date":
                    if (arg == null) throw new IllegalArgumentException("Требуется дата");
                    sendCommand(new FilterLessThanEndDateCmd(arg));
                    break;
                case "history":
                    System.out.println("Последние команды:");
                    history.forEach(cmd -> System.out.println("  " + cmd));
                    break;   // не отправляем на сервер
                case "execute_script":
                    if (arg == null) throw new IllegalArgumentException("Укажите имя файла");
                    executeScript(arg);
                    break;
                case "exit":
                    System.out.println("Завершение клиента.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите help.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отправляет команду на сервер и выводит ответ.
     */
    private void sendCommand(Commands command) {
        try {
            Response response = sender.send(new Request(command));
            System.out.println(response.getMessage());
            if (response.getWorkers() != null) {
                response.getWorkers().forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Ошибка связи с сервером: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка десериализации ответа: " + e.getMessage());
        }
    }

    /**
     * Локальная обработка команды execute_script.
     * Читает файл и выполняет каждую строку через этот же диспетчер.
     */
    private void executeScript(String fileName) {
        if (scriptDepth >= MAX_SCRIPT_DEPTH) {
            System.out.println("Превышена максимальная глубина рекурсии скриптов (max = " + MAX_SCRIPT_DEPTH + ")");
            return;
        }
        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Ошибка: файл не существует или недоступен для чтения.");
            return;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            scriptDepth++;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                System.out.println("> " + line);
                dispatch(line);   // рекурсивный вызов dispatch
            }
            scriptDepth--;
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден.");
        }
    }
}