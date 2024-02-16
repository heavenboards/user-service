package heavenboards.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Главный класс для запуска сервиса.
 */
@SpringBootApplication(scanBasePackages = {
    "heavenboards.user.service",
    "security.service",
    "transfer.contract"
})
@EnableFeignClients(basePackages = "transfer.contract")
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
