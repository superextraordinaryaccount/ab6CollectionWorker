package servermod;

import enviroment.Response;
import enviroment.workerclass.Worker;
import enviroment.commands.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class CommandHandler {
    private final CollectionManager collectionManager;
    public CommandHandler(CollectionManager cm) {
        this.collectionManager = cm;
    }


    public Response handle(Commands command) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            command.execute(collectionManager, ps);
            ps.flush();
            String message = baos.toString();
            // Если команда требует отправки отсортированной коллекции
            List<Worker> workers = null;
            if (command instanceof ShowCmd || command instanceof FilterLessThanEndDateCmd ) {
                workers = collectionManager.getSortedBySalary();
            }
            return new Response(message, workers);
        } catch (Exception e) {
            return new Response("Ошибка выполнения: " + e.getMessage());
        }
    }
}
