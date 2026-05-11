package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Интерфейс команд
 */
public interface Commands extends Serializable {
    void execute(CollectionManager collectionManager, PrintStream out) throws Exception;

    String getName();
}
