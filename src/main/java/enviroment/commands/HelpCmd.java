package enviroment.commands;

import servermod.CollectionManager;

import java.io.PrintStream;

/**
 * Команда help - выдаёт списочную и справочную информации
 */
public class HelpCmd implements Commands {
    private transient String secret = "qwerty";
//Как не отправлять на сервер некие поля, аннотация, слово,private; Exrernaliziable
    @Override
    public void execute(CollectionManager collectionManager, PrintStream ps) {
        ps.println(""" 
                Доступные команды:
                help - справка о командах
                info - информация о коллекции
                show - все элементы коллекции
                insert <key> - добавить новый элемент с ключом
                update <id> - обновить элемент по id
                remove_key <key> - удалить элемент по ключу
                clear - очистить коллекцию
                execute_script <file> - выполнить скрипт из файла
                exit - завершить программу
                remove_greater - удалить элементы, превышающие заданный (по зарплате)
                remove_lower - удалить элементы, меньшие заданного (по зарплате)
                history - последние 12 команд
                max_by_end_date - элемент с максимальным endDate
                count_by_person - количество элементов с заданным Person
                filter_less_than_end_date <endDate> - элементы с endDate меньше заданного""");
    }

    public String getName() {
        return "help";
    }


}