package enviroment.commands;

import servermod.CollectionManager;


import java.io.PrintStream;

/**
 * Класс команды, удаляющей элемент по ключу
 */
public class RemoveKeyCmd implements Commands {
    private final String args;

    public RemoveKeyCmd(String args) {
        this.args = args;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        if (args == null) {
            ps.println("Ошибка: укажите ключ. Пример: remove_key key123");
            return;
        }
        String key = args.trim();
        if (collectionManager.remove(key) != null) {
            ps.println("Элемент удалён.");
        } else {
            ps.println("Ключ не найден.");
        }

    }

    public String getName() {
        return "remove_key";
    }
}
