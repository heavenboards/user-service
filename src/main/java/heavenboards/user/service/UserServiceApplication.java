package heavenboards.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс для запуска сервиса.
 */
@SpringBootApplication
public class UserServiceApplication {
    /**
     * Главный метод для запуска сервиса.
     *
     * @param args - аргументы запуска
     */
    public static void main(final String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
