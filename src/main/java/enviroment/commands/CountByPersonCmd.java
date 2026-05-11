package enviroment.commands;

import servermod.CollectionManager;
import clientmod.InputManager;
import enviroment.workerclass.Person;

import java.io.PrintStream;

/**
 * Класс, ответственный за счёт работников по Person
 * (Если один человек увольнялся или устраивался на работу несколько раз)
 */
public class CountByPersonCmd implements Commands {
    private final Person person;

    public CountByPersonCmd( Person person) {
        this.person = person;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        long count = collectionManager.countByPerson(person);
        ps.println("Количество элементов с заданным Person: " + count);
    }

    @Override
    public String getName() {
        return "count_by_person";
    }

}
