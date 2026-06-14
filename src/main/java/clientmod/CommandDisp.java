package clientmod;

import enviroment.Request;
import enviroment.Response;
import enviroment.commands.*;
import enviroment.workerclass.Person;
import enviroment.workerclass.Worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Диспетчер команд клиента.
 * Преобразует строки в объекты Command, отправляет их на сервер и выводит результат.
 */
public class CommandDisp {
    private final CommandSender sender;
    private final InputManager inputManager;
    private int scriptDepth = 0;
    private static final int MAX_SCRIPT_DEPTH = 10;
    private final Deque<String> history = new ArrayDeque<>(12);
    private final Map<String, CommandWrap> commandMap = new HashMap<>();

    public CommandDisp(CommandSender sender, InputManager inputManager) {
        this.sender = sender;
        this.inputManager = inputManager;
        initCommands();
    }

    private void addToHistory(String cmdName) {
        history.addFirst(cmdName);
        if (history.size() > 12) history.removeLast();
    }

    private void initCommands() {
        // Команды, отправляемые на сервер
        commandMap.put("info", (arg, in) -> new InfoCmd());
        commandMap.put("show", (arg, in) -> new ShowCmd());
        commandMap.put("clear", (arg, in) -> new ClearCmd());
        commandMap.put("max_by_end_date", (arg, in) -> new MaxByEndDateCmd());
        commandMap.put("remove_key", (arg, in) -> new RemoveKeyCmd(arg));
        commandMap.put("update", (arg, in) -> {
            if (arg == null) throw new IllegalArgumentException("Ошибка: укажите id. Пример: update 5");
            Worker worker = in.readWorker();
            return new UpdateCmd(arg, worker);
        });
        commandMap.put("insert", (arg, in) -> {
            String key = (arg != null) ? arg : in.readString("Ошибка: укажите ключ. Пример: insert key123", false);
            Worker worker = in.readWorker();
            return new InsertCmd( worker,key);
        });
        commandMap.put("remove_greater", (arg, in) -> {
            Worker threshold = in.readWorker();
            return new RemoveGreaterCmd(threshold);
        });
        commandMap.put("remove_lower", (arg, in) -> {
            Worker threshold = in.readWorker();
            return new RemoveLowerCmd(threshold);
        });
        commandMap.put("count_by_person", (arg, in) -> {
            Person person = in.readPerson();
            return new CountByPersonCmd(person);
        });
        commandMap.put("filter_less_than_end_date", (arg, in) -> {
            if (arg == null) throw new IllegalArgumentException("Требуется дата в формате ISO, " +
                    "например 2023-12-31T10:15:30+01:00[Europe/Paris]): ");
            return new FilterLessThanEndDateCmd(arg);
        });
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


        switch (cmdName) {
                case "help":
                    sendCommand(new HelpCmd());
                    return;
                case "history":
                    System.out.println("Последние команды:");
                    history.forEach(cmd -> System.out.println("  " + cmd));
                    return;   // не отправляем на сервер
                case "execute_script":
                    if (arg == null) throw new IllegalArgumentException("Укажите имя файла");
                    executeScript(arg);
                    return;
                case "exit":
                    System.out.println("Завершение клиента.");
                    System.exit(0);
                    return;
            }
        // Остальные команды берём из мапы
        CommandWrap factory = commandMap.get(cmdName);
        if (factory == null) {
            System.out.println("Неизвестная команда. Введите help.");
            return;
        }

        try {
            Commands command = factory.create(arg, inputManager);
            if (command != null) {
                sendCommand(command);
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
            System.out.println("Ошибка: файл не существует или он недоступен для чтения.");
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