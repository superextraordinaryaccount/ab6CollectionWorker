package enviroment.commands;

import servermod.CollectionManager;
import enviroment.workerclass.Worker;
import java.io.PrintStream;

public class UpdateCmd implements Commands {
    private final String args;
    private final Worker newWorker;

    public UpdateCmd(String args, Worker newWorker) {
        this.args = args;
        this.newWorker = newWorker;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        if (args == null) {
            ps.println("Ошибка: укажите id. Пример: update 5");
            return;
        }
        long id;
        try {
            id = Long.valueOf(args.trim());
        } catch (NumberFormatException e) {
            ps.println("Ошибка: id должно быть числом типа long.");
            return;
        }
        Worker old = collectionManager.getById(id);
        if (old == null) {
            ps.println("Элемент с id " + id + " не найден.");
            return;
        }
        // Сохраняеются старые id и creationDate
        newWorker.setId(old.getId());
        newWorker.setCreationDate(old.getCreationDate());
        // Обновляется в коллекции
        String key = collectionManager.getKeyById(id);
        collectionManager.add(key, newWorker);
        ps.println("Элемент обновлён.");
    }

    @Override
    public String getName() {
        return "update";
    }
}
