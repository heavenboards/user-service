package heavenboards.user.service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
@OpenAPIDefinition(
    info = @Info(
        title = "user-service",
        version = "1.0.0",
        description = "Микросервис для взаимодействия с данными пользователей"
    )
)
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
