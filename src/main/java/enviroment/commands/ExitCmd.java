package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;

/**
 * Класс, ответственный за выход из программы
 */
public class ExitCmd implements Commands {
    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        ps.println("Завершение программы.");
        System.exit(0);
    }

    @Override
    public String getName() {
        return "exit";
    }
}
