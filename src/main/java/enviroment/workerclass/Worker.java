package enviroment.workerclass;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;


/**
 * Класс, представляющий работника.
 * Содержит следующие поля: id, ФИО, Местоположение, Дата создания профиля,
 * Заработная плата, Дата начала отсчёта времени, конечная дата отсчёта времени,
 * Должность, человек
 * Естественная сортировка реализована по id.
 */
public class Worker implements Comparable<Worker>, Serializable {
    private long id; // Значение поля должно быть больше 0, уникально, генерируется автоматически
    private String name; // Поле не может быть null, строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private LocalDate creationDate; // Поле не может быть null, генерируется автоматически
    private long salary; // Значение поля должно быть больше 0
    private Date startDate; // Поле не может быть null
    private ZonedDateTime endDate; // Поле может быть null
    private Position position; // Поле может быть null
    private Person person; // Поле не может быть null

    /**
     * У резанный конструктор для создания нового работника (без id и creationDate).
     * id и creationDate будут сгенерированы автоматически
     */
    public Worker(String name, Coordinates coordinates, long salary, Date startDate,
                  ZonedDateTime endDate, Position position, Person person) {
        setName(name);
        setCoordinates(coordinates);
        setSalary(salary);
        setStartDate(startDate);
        setEndDate(endDate);
        setPosition(position);
        setPerson(person);
    }

    /**
     * Полный конструкто Worker для загрузки из CSV-файла.
     */
    public Worker(long id, String name, Coordinates coordinates, LocalDate creationDate,
                  long salary, Date startDate, ZonedDateTime endDate, Position position, Person person) {
        this(name, coordinates, salary, startDate, endDate, position, person);
        setId(id);
        setCreationDate(creationDate);
    }

    // Геттеры и сеттеры с валидацией(проверкой)

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id <= 0) throw new IllegalArgumentException("id должно быть не отрицательным");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Не указано имя");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("НЕ известно местоположение");
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        if (creationDate == null) throw new IllegalArgumentException("Не указана Дата создания");
        this.creationDate = creationDate;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        if (salary <= 0) throw new IllegalArgumentException("Зарплата должна быть больше 0");
        this.salary = salary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate == null) throw new IllegalArgumentException("Дата начала отсчёта времени не указана");
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        if (person == null) throw new IllegalArgumentException("Person не может быть null");
        this.person = person;
    }

    @Override
    public int compareTo(Worker o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return String.format("Worker{id=%d, name='%s', coordinates=%s, creationDate=%s, salary=%d, startDate=%s, endDate=%s, position=%s, person=%s}",
                id, name, coordinates, creationDate, salary, startDate, endDate, position, person);
    }
}