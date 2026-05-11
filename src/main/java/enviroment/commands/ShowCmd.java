package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;

/**
 * класс, ответственный за команду Show - показывает информацию о коллекции
 */
public class ShowCmd implements Commands {


    public ShowCmd() {
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        if (collectionManager.insIsEmpty()) {
            ps.println("Коллекция пуста.");
        } else {
            collectionManager.showInfoAbCol();
        }
    }

    @Override
    public String getName() {
        return "show";
    }
}
