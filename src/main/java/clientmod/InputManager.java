package clientmod;

import enviroment.workerclass.*;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 * Обеспечивает ввод пользователем объектов с валидацией(проверкой) и повторными попытками.
 */
public class InputManager {
    private final Scanner scanner;


    public InputManager(Scanner scanner) {
        this.scanner = scanner;

    }

    /**
     * Читает строку, не может быть пустой (если required = true).
     */
    public String readString(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (required && line.isEmpty()) {
                System.out.println("Ошибка: значение не может быть пустым.");
                continue;
            }
            return line;
        }
    }

    /**
     * Читает целое число long с проверкой >0.
     */
    public long readLong(String prompt, long min, boolean positive) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                long val = Long.valueOf(line);
                if (positive && val <= 0) {
                    System.out.println("Ошибка: значение должно быть > 0.");
                    continue;
                }
                if (val < min) {
                    System.out.println("Ошибка: значение должно быть >= " + min);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    /**
     * Читает число Double, не может быть null.
     */
    public Double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.valueOf(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число с плавающей точкой.");
            }
        }
    }

    /**
     * Читает число Float с проверкой максимума.
     */
    public Float readFloat(String prompt, float max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                float val = Float.valueOf(line);
                if (val > max) {
                    System.out.println("Ошибка: значение не должно превышать " + max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число с плавающей точкой.");
            }
        }
    }

    /**
     * Читает enum с возможностью null.
     */
    public <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, boolean allowNull) {
        T[] constants = enumClass.getEnumConstants();
        System.out.println("Допустимые значения: " + Arrays.toString(constants));
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (allowNull && line.isEmpty()) {
                return null;
            }
            try {
                return Enum.valueOf(enumClass, line.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите одно из допустимых значений.");
            }
        }
    }

    /**
     * Читает дату LocalDate.
     */
    public LocalDate readLocalDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: введите дату в формате yyyy-MM-dd.");
            }
        }
    }

    /**
     * Читает Date (java.util.Date) в формате yyyy-MM-dd.
     */
    public Date readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String line = scanner.nextLine().trim();
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(line);
            } catch (java.text.ParseException e) {
                System.out.println("Ошибка: введите дату в формате yyyy-MM-dd.");
            }
        }
    }

    /**
     * Читает ZonedDateTime, может быть null.
     */
    public ZonedDateTime readZonedDateTime(String prompt, boolean allowNull) {
        while (true) {
            System.out.print(prompt + " (ISO формат, например 2023-12-31T10:15:30+01:00[Europe/Paris]): ");
            String line = scanner.nextLine().trim();
            if (allowNull && line.isEmpty()) {
                return null;
            }
            try {
                return ZonedDateTime.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: введите дату в правильном ISO формате.");
            }
        }
    }

    /**
     * Читает объект Coordinates.
     */
    public Coordinates readCoordinates() {
        Double x = readDouble("Введите координату X (Double): ");
        Float y = readFloat("Введите координату Y (Float, <=289): ", 289f);
        return new Coordinates(x, y);
    }

    /**
     * Читает объект Person.
     */
    public Person readPerson() {
        LocalDate birthday = readLocalDate("Введите дату рождения");
        Color eyeColor = readEnum("Введите цвет глаз (может быть пустым): ", Color.class, true);
        Color hairColor = readEnum("Введите цвет волос: ", Color.class, false);
        Country nationality = readEnum("Введите национальность: ", Country.class, false);
        return new Person(birthday, eyeColor, hairColor, nationality);
    }

    /**
     * Читает объект Worker (без id и creationDate).
     */
    public Worker readWorker() {
        String name = readString("Введите имя: ", true);
        Coordinates coordinates = readCoordinates();
        long salary = readLong("Введите зарплату (long >0): ", 1, true);
        Date startDate = readDate("Введите дату начала работы");
        ZonedDateTime endDate = readZonedDateTime("Введите дату окончания (может быть пустой): ", true);
        Position position = readEnum("Введите должность (может быть пустой): ", Position.class, true);
        Person person = readPerson();
        return new Worker(name, coordinates, salary, startDate, endDate, position, person);
    }
}
