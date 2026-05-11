package enviroment.commands;

import servermod.CollectionManager;
import enviroment.workerclass.Worker;

import java.io.PrintStream;

/**
 * Класс, ответственный за добавление нового работника
 */
public class InsertCmd implements Commands {
    private final String args;
    private final Worker worker;

    public InsertCmd(  Worker worker, String args) {
        this.args=args;
        this.worker = worker;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {

        String key = args.trim();


        if (collectionManager.isColContainsKey(key)) {
            ps.println("Ошибка: ключ уже существует.");
            return;
        }
        collectionManager.add(key, worker);
        ps.println("Элемент добавлен с id " + worker.getId());
    }

    @Override
    public String getName() {
        return "insert";
    }
}
