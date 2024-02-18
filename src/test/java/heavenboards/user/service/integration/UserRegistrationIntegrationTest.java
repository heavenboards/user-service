package heavenboards.user.service.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import security.service.jwt.JwtTokenExtractor;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.authentication.TokenResponseTo;
import transfer.contract.domain.error.ServerErrorCode;
import transfer.contract.domain.error.ServerErrorTo;

/**
 * Интеграционные тесты для регистрации пользователей.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class UserRegistrationIntegrationTest {
    /**
     * Порт приложения.
     */
    @LocalServerPort
    private int port;

    /**
     * Класс для извлечения данных из JWT-токенов.
     */
    @Autowired
    private JwtTokenExtractor tokenExtractor;

    /**
     * Конфигурация класса перед тестами.
     */
    @BeforeAll
    public void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1/auth";
    }

    /**
     * Тест валидной регистрации.
     */
    @Test
    @DisplayName("Тест валидной регистрации")
    public void validRegistrationTest() {
        String username = "username";
        Response response = registerUserAndGetResponse(username);

        TokenResponseTo tokenResponse = response
            .getBody()
            .as(TokenResponseTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertNotNull(tokenResponse);
        Assertions.assertNotNull(tokenResponse.getToken());
        Assertions.assertEquals(username, tokenExtractor
            .extractUsername(tokenResponse.getToken()));
    }

    /**
     * Тест валидной регистрации.
     */
    @Test
    @DisplayName("Тест занятого username")
    public void usernameAlreadyTakenTest() {
        String username = "username";
        registerUserAndGetResponse(username);
        Response response = registerUserAndGetResponse(username);

        ServerErrorTo serverErrorTo = response
            .getBody()
            .as(ServerErrorTo.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertNotNull(serverErrorTo);
        Assertions.assertNotNull(serverErrorTo.getErrorCode());
        Assertions.assertEquals(ServerErrorCode.USERNAME_ALREADY_EXIST, serverErrorTo
            .getErrorCode());
    }

    /**
     * Зарегистрировать пользователя и получить ответ.
     *
     * @param username - имя пользователя
     * @return ответ
     */
    private Response registerUserAndGetResponse(String username) {
        RegistrationRequestTo body = RegistrationRequestTo.builder()
            .username(username)
            .password("pAssw0rd123!")
            .firstName("Ivan")
            .lastName("Ivanov")
            .build();

        return RestAssured
            .given()
            .contentType("application/json")
            .body(body)
            .when()
            .post("/register");
    }
}
