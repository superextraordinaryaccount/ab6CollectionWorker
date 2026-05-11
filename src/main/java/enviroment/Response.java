package enviroment;

import enviroment.workerclass.Worker;

import java.io.Serializable;
import java.util.List;


public class Response implements Serializable {
    private final String message;
    private final List<Worker> workers;  // может быть null, если команда не возвращает коллекцию

    public Response(String message) {
        this(message, null);
    }

    public Response(String message, List<Worker> workers) {
        this.message = message;
        this.workers = workers;
    }

    public String getMessage() {
        return message;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    // Удобный метод для получения отсортированных работников (если нужна сортировка на клиенте)
    // Но лучше, чтобы сервер возвращал уже отсортированный список.
    public List<Worker> getSortedWorkers() {
        if (workers == null) return null;
        // Сортировка по зарплате (или по id) – можно сделать на клиенте, но лучше на сервере
        workers.sort(java.util.Comparator.comparingLong(Worker::getSalary));
        return workers;
    }
}
