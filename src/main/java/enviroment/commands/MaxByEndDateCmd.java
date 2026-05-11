package enviroment.commands;

import servermod.CollectionManager;
import enviroment.workerclass.Worker;

import java.io.PrintStream;

public class MaxByEndDateCmd implements Commands {

    public MaxByEndDateCmd() {
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        Worker maxWorker = null;
        java.time.ZonedDateTime maxDate = null;
        maxWorker=collectionManager.MaxByEndDate(maxWorker,maxDate);
        if (maxWorker != null) {
            ps.println("Элемент с максимальным endDate: " + maxWorker);
        } else {
            ps.println("Нет элемента, в котором указана EndDate.");
        }
    }

    @Override
    public String getName() {
        return "max_by_end_date";
    }
}
