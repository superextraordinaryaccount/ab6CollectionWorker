package enviroment.commands;

import servermod.CollectionManager;


import java.io.PrintStream;

/**
 * Команда info - выдаёт информацию о коллекции
 */
public class InfoCmd implements Commands {

    public InfoCmd() {
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        ps.printf("""
                        Тип коллекции: %s%n
                        Дата инициализации: %s%n
                        Количество элементов: %s%n
                        """, collectionManager.getColType(),
                collectionManager.getInitializationDate(),
                collectionManager.getSize());

    }

    @Override
    public String getName() {
        return "info";
    }


}
