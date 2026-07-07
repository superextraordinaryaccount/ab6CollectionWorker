package clientmod;

import enviroment.Request;
import enviroment.Response;
import enviroment.commands.ShowCmd;

public class RateLimitTest {
    public static void main(String[] args) {
        //String login = "Tim";          // для переноса на след лабы
        //String password = "1234";      //
        CommandSender sender = new CommandSender("localhost", 8888, 500, 1);
// Таймаут уменьшен для ускорения
        int totalRequests = 20;
        int accepted = 0;
        long start = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            try {
                Request request = new Request(new ShowCmd());
                //Request request = new Request(new ShowCmd(), login, password); - если для след лаб
                Response response = sender.send(request);
                // Если ответ пришёл – выводит сообщение
                accepted++;
                System.out.println("Запрос " + (i+1) + " -> принят сервером (" + response.getMessage() + ")");

            } catch (Exception e) {
                System.out.println("Запрос " + (i+1) + " -> Ошибка: " + e.getMessage());
            }
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("Отправлено " + totalRequests + " запросов за " + duration + " мс");
        System.out.println("Принято сервером: " + accepted);
    }
}
