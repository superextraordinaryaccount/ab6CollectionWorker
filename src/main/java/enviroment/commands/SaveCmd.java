package enviroment.commands;

import servermod.CollectionManager;
import servermod.FileManager;

import java.io.IOException;
import java.io.PrintStream;

public class SaveCmd implements Commands {
    private final FileManager fileManager;

    public SaveCmd( FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        try {
            collectionManager.saveInFile(fileManager);
            ps.println("Коллекция сохранена в файл.");
        } catch (IOException e) {
            ps.println("Ошибка сохранения: " + e.getMessage());
            return;
        }
    }

    @Override
    public String getName() {
        return "save";
    }
}
