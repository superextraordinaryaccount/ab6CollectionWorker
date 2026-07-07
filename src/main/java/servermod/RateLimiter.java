package servermod;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Простой ограничитель частоты запросов на основе фиксированного окна (секунда).
 * Для каждого IP хранится количество запросов в текущую секунду.
 * При превышении лимита запрос отклоняется.
 * Старые записи периодически очищаются.
 */
public class RateLimiter {
    private final int maxRequestsPerSecond;
    private final ConcurrentHashMap<InetAddress, RateInfo> counters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    // Структура для хранения состояния по IP
    private static class RateInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long currentWrittenSecond;

        RateInfo(long second) {
            this.currentWrittenSecond = second;
        }
    }

    public RateLimiter(int maxRequestsPerSecond) {
        this.maxRequestsPerSecond = maxRequestsPerSecond;
        // Запуск очистки старых записей раз в 30 секунд (удаляем записи, которые старше 5 секунд)
        cleaner.scheduleAtFixedRate(this::cleanup, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * Проверяет, может ли запрос с данного адреса быть обработан.
     * @param clientAddr адрес клиента (InetSocketAddress)
     * @return true, если запрос разрешён, иначе false
     */
    public boolean allowRequest(SocketAddress clientAddr) {
        if (!(clientAddr instanceof InetSocketAddress)){
            // Если адрес неизвестного типа, не пропускать
            return false;
        }
        InetAddress address = ((InetSocketAddress) clientAddr).getAddress();
        long nowSec = System.currentTimeMillis() / 1000;

        RateInfo info = counters.computeIfAbsent(address, k -> new RateInfo(nowSec));
        long lastSec = info.currentWrittenSecond;

        if (lastSec != nowSec) {
            // Переход на новую секунду – сбрасываем счётчик и обновляем время
            // Проверяем, не изменилась ли запись - другой поток не сбросил таймер,
            // чтобы избежать race condition при параллельных запросах
            synchronized (info) {
                if (info.currentWrittenSecond == lastSec) {
                    info.currentWrittenSecond = nowSec;
                    info.count.set(0);
                }
            }
        }
        int currentCount = info.count.incrementAndGet();
        return currentCount <= maxRequestsPerSecond;
    }

    /**
     * Очищает записи для IP, которые не обращались более 5 секунд.
     */
    private void cleanup() {
        long nowSec = System.currentTimeMillis() / 1000;
        counters.entrySet().removeIf(entry -> {
            long last = entry.getValue().currentWrittenSecond;
            return nowSec - last > 5;
        });
    }

    /**
     * Останавливает работу очистителя (при завершении сервера).
     */
    public void shutdown() {
        cleaner.shutdownNow();
    }
}
