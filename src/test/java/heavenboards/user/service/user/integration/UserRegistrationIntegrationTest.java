package heavenboards.user.service.user.integration;

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
import transfer.contract.domain.authentication.AuthenticationOperationErrorCode;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.user.UserTo;

import java.util.List;

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

        AuthenticationOperationResultTo operationResult = response
            .getBody()
            .as(AuthenticationOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());
        Assertions.assertEquals(username, tokenExtractor
            .extractUsername(operationResult.getToken()));
    }

    /**
     * Тест валидной регистрации.
     */
    @Test
    @DisplayName("Тест занятого username")
    public void usernameAlreadyTakenTest() {
        String username = "username";

        Response successResponse = registerUserAndGetResponse(username);
        AuthenticationOperationResultTo successResult = successResponse
            .getBody()
            .as(AuthenticationOperationResultTo.class);

        Response failedResponse = registerUserAndGetResponse(username);
        AuthenticationOperationResultTo failedResult = failedResponse
            .getBody()
            .as(AuthenticationOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), failedResponse.getStatusCode());
        Assertions.assertEquals(OperationStatus.FAILED, failedResult.getStatus());
        Assertions.assertEquals(List.of(
            AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                .builder()
                .failedUserId(successResult.getUserId())
                .errorCode(AuthenticationOperationErrorCode.USERNAME_ALREADY_EXIST)
                .build()
        ), failedResult.getErrors());
    }

    /**
     * Зарегистрировать пользователя и получить ответ.
     *
     * @param username - имя пользователя
     * @return ответ
     */
    private Response registerUserAndGetResponse(String username) {
        UserTo body = UserTo.builder()
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
