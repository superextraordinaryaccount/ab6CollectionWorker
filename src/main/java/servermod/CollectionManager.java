package servermod;

import enviroment.workerclass.Worker;
import enviroment.workerclass.Person;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Управляет коллекцией Hashtable, хранит дату инициализации, генерирует id.
 */
public class CollectionManager {
    private final Hashtable<String, Worker> collection = new Hashtable<>();
    private final LocalDate initializationDate = LocalDate.now();
    private long currentMaxId = 0;


    public CollectionManager() {


    }

    /**
     * Возвращает коллекцию.
     */
    public Hashtable<String, Worker> getCollection() {
        return collection;
    }

    /**
     * Возвращает дату инициализации.
     */
    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    /**
     * Генерирует новый уникальный id (больше всех существующих).
     */
    public long generateId() {
        return ++currentMaxId;
    }

    /**
     * Обновляет максимальный id на основе текущей коллекции.
     * Вызывается после загрузки.
     */
    public void updateMaxId() {

       currentMaxId = collection.values().isEmpty() ? 0:
               collection.values().stream().mapToLong(Worker::getId).max().getAsLong();

    }


    /**
     * Добавляет элемент по ключу, устанавливая ему сгенерированный id и текущую дату.
     */
    public void add(String key, Worker worker) {
        worker.setId(generateId());
        worker.setCreationDate(LocalDate.now());
        collection.put(key, worker);
    }

    public void add(Hashtable<String,Worker> col){
        collection.putAll(col);
    }

    /**
     * Обновляет существующий элемент по id (поля, кроме id и creationDate).
     */
    public boolean updateById(long id, Worker newWorker) {
        for (String key : collection.keySet()) {
            if (collection.get(key).getId() == id) {
                Worker old = collection.get(key);
                old.setName(newWorker.getName());
                old.setCoordinates(newWorker.getCoordinates());
                old.setSalary(newWorker.getSalary());
                old.setStartDate(newWorker.getStartDate());
                old.setEndDate(newWorker.getEndDate());
                old.setPosition(newWorker.getPosition());
                old.setPerson(newWorker.getPerson());
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет элемент по ключу.
     *
     * @return
     */
    public Worker remove(String key) {
        Worker bay=collection.remove(key);
        return bay;
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        collection.clear();
        currentMaxId = 0;
    }

    /**
     * Возвращает элемент по id или null.
     */
    public Worker getById(long id) {
        return collection.values().stream()
                .filter(w -> w.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает ключ по id.
     */
    public String getKeyById(long id) {
        return collection.entrySet().stream()
                .filter(e -> e.getValue().getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Методы, работаюшие с коллекцией, необходимые для комманд
     * @return
     */
    public Set<Map.Entry<String, Worker>> colEntrySet(){
        return collection.entrySet();
    }

    public boolean insIsEmpty(){
        return collection.isEmpty();
    }

    public void showInfoAbCol(){
        collection.forEach((key, worker) ->
                System.out.println(key + ":" + worker));
    }

    public int getSize(){
        return collection.size();
    }

    public String getColType(){
        return collection.getClass().getSimpleName();
    }

    public long countByPerson(Person person){
        return collection.values().stream()
                .filter(worker -> worker.getPerson().equals(person)).count();
    }

    public List<String> compareSalary(long salaryThreshold, String sign){
        List<String> KeysToRemove = new ArrayList<>();
        if (Objects.equals(sign, ">")){
        for (Map.Entry<String, Worker> entry : collection.entrySet()) {
            if (entry.getValue().getSalary() > salaryThreshold) {
                KeysToRemove.add(entry.getKey());
            }}}
        if (Objects.equals(sign, "<")){
            for (Map.Entry<String, Worker> entry : collection.entrySet()) {
                if (entry.getValue().getSalary() < salaryThreshold) {
                    KeysToRemove.add(entry.getKey());
                }}}
        return KeysToRemove;
    }

    public Worker MaxByEndDate(Worker maxWorker, ZonedDateTime maxDate){
        for (Worker worker : collection.values()) {
            if (worker.getEndDate() != null) {
                if (maxDate == null || worker.getEndDate().isAfter(maxDate)) {
                    maxDate = worker.getEndDate();
                    maxWorker = worker;
                }
            }
        }
        return maxWorker;
    }

    public void filterLessThanEndDate(ZonedDateTime date ){
        collection.values().stream()
                .filter(worker -> worker.getEndDate() != null && worker.getEndDate().isBefore(date))
                .forEach(System.out::println);
    }

    public boolean isColContainsKey(String key){
        return collection.containsKey(key);
    }

    public void saveInFile(FileManager fileManager) throws IOException {
        fileManager.save(collection);
    }

    public List<Worker> getSortedBySalary() {
        return collection.values().stream()
                .sorted(Comparator.comparingLong(Worker::getSalary))
                .collect(Collectors.toList());
    }
}
