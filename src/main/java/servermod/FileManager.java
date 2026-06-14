package servermod;


import enviroment.workerclass.*;

import java.io.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Читает и записывает коллекцию в CSV-файл.
 * Конструктор Файлового менеджера
 */
public class FileManager {
    private final String filename;

    public FileManager(String filename) {
        this.filename = filename;
    }

    /**
     * Загружает коллекцию  Hashtable из csv-файла. Если строка не корректна,
     * то выводит сообщение об ошибке и приступает к следующей
     */
    public Hashtable<String, Worker> load() {
        Hashtable<String, Worker> loaded = new Hashtable<>();
        if (filename == null) {
            System.err.println("Имя файла не задано.");
        } else {
            try (Scanner scanner = new Scanner(new File(filename))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    try {
                        Worker worker = convertToWorker(line);
                        String[] parts = line.split(";", -1);
                        // Ключ - первый элемент строки
                        String k = parts[0];
                        loaded.put(k, worker);
                    } catch (Exception e) {
                        System.err.println("Ошибка преобразования строки: " + line + " - " + e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Файл не найден: " + filename + ", будет создан при сохранении.");
            }
        }
        return loaded;
    }

    /**
     * Сохраняет коллекцию в файл.
     */
    public void save(Hashtable<String, Worker> collection) throws IOException {
        if (filename == null) {
            throw new IOException("Имя файла не задано.");
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream))) {
            for (String key : collection.keySet()) {
                Worker worker = collection.get(key);
                writer.println(workerToString(key, worker));
            }
        }
    }
    /**
     * Преобразование строки csv-файла в обЪект Worker, также проверка строки
    */
    private Worker convertToWorker(String csvLine) {
        String[] parts = csvLine.split(";", -1);
        if (parts.length != 14) throw new IllegalArgumentException("Произошла ошибка с количеством полей");

        String k = parts[0]; // ключ используется не здесь
        long id = Long.valueOf(parts[1]);
        String name = parts[2];
        Double x = Double.valueOf(parts[3]);
        Float y = Float.valueOf(parts[4]);
        LocalDate creationDate = LocalDate.parse(parts[5]);
        long salary = Long.valueOf(parts[6]);
        Date startDate = convertToDate(parts[7]);
        ZonedDateTime endDate = parts[8].isEmpty() ? null : ZonedDateTime.parse(parts[8]);
        Position position = parts[9].isEmpty() ? null : Position.valueOf(parts[9]);
        LocalDate birthday = LocalDate.parse(parts[10]);
        Color eyeColor = parts[11].isEmpty() ? null : Color.valueOf(parts[11]);
        Color hairColor = Color.valueOf(parts[12]);
        Country nationality = Country.valueOf(parts[13]);

        Coordinates coordinates = new Coordinates(x, y);
        Person person = new Person(birthday, eyeColor, hairColor, nationality);
        Worker worker = new Worker(id, name, coordinates, creationDate, salary, startDate, endDate, position, person);
        return worker;
    }

    private String workerToString(String key, Worker w) {
        return String.join(";",
                key,
                String.valueOf(w.getId()),
                w.getName(),
                String.valueOf(w.getCoordinates().getX()),
                String.valueOf(w.getCoordinates().getY()),
                w.getCreationDate().toString(),
                String.valueOf(w.getSalary()),
                dateToString(w.getStartDate()),
                w.getEndDate() == null ? "" : w.getEndDate().toString(),
                w.getPosition() == null ? "" : w.getPosition().name(),
                w.getPerson().getBirthday().toString(),
                w.getPerson().getEyeColor() == null ? "" : w.getPerson().getEyeColor().name(),
                w.getPerson().getHairColor().name(),
                w.getPerson().getNationality().name()

        );
    }

    private Date convertToDate(String s) {
        try {
            // Ожидается формат yyyy-MM-dd
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(s);
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Использован неверный формат даты: " + s);
        }
    }

    private String dateToString(Date d) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
}
