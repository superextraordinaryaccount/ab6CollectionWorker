package enviroment.commands;

import enviroment.workerclass.Worker;
import servermod.CollectionManager;
import clientmod.InputManager;

import java.io.PrintStream;
import java.util.List;

public class RemoveGreaterCmd implements Commands {
    private final Worker salaryThresholdWorker;

    public RemoveGreaterCmd(Worker salaryThreshold) {
        this.salaryThresholdWorker = salaryThreshold;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
/*        String prompt = "Введите пограничное значение зарплаты"; то работа с Input manager
        long salaryThreshold = inputManager.readLong(prompt, 1, true);

*/      long salaryThreshold=salaryThresholdWorker.getSalary();
        List<String> KeysToRemove=collectionManager.compareSalary(salaryThreshold,">");
        for (String keys : KeysToRemove) {
            collectionManager.remove(keys);
        }
        ps.println("Удалены элементы с зарплатой больше чем " + salaryThreshold);
    }

    @Override
    public String getName() {
        return "remove_greater";
    }
}
