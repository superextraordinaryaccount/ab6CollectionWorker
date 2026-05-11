package enviroment.commands;

import enviroment.workerclass.Worker;
import servermod.CollectionManager;


import java.io.PrintStream;
import java.util.List;

public class RemoveLowerCmd implements Commands {
    private final Worker salaryThresholdWorker;

    public RemoveLowerCmd(Worker salaryThreshold) {
        this.salaryThresholdWorker = salaryThreshold;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
/*        String prompt = "Введите пограничное значение зарплаты";
        long salaryThreshold = inputManager.readLong(prompt, 1, true); */
        long salaryThreshold=salaryThresholdWorker.getSalary();
        List<String> KeysToRemove=collectionManager.compareSalary(salaryThreshold,"<");
        for (String keys : KeysToRemove) {
            collectionManager.remove(keys);
        }
        ps.println("Удалены элементы с зарплатой меньше чем " + salaryThreshold);
    }

    @Override
    public String getName() {
        return "remove_lower";
    }
}
