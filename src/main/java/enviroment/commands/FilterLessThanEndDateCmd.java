package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class FilterLessThanEndDateCmd implements Commands {
    private final String args;

    public FilterLessThanEndDateCmd(String date) {
        this.args = date;
    }

    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        ZonedDateTime date;
        try {
            date = ZonedDateTime.parse(args);
        } catch (DateTimeParseException e) {
            ps.println("Ошибка: неверный формат даты. Используйте ISO формат.");
            return;
        }
        collectionManager.filterLessThanEndDate(date);
    }

    @Override
    public String getName() {
        return "filter_less_than_end_date";
    }
}
