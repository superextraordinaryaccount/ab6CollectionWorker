package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;

/**
 * Класс, ответственный за очистку коллекции
 */
public class ClearCmd implements Commands {
    public ClearCmd() {
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        collectionManager.clear();
        ps.println("Коллекция очищена.");
    }

    @Override
    public String getName() {
        return "clear";
    }

}
