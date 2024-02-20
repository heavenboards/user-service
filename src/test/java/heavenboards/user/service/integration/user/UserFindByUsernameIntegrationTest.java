package heavenboards.user.service.integration.user;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import transfer.contract.exception.ApplicationException;
import transfer.contract.exception.BaseErrorCode;

/**
 * Интеграционный тест поиска пользователя по username.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserFindByUsernameIntegrationTest {
    /**
     * Порт приложения.
     */
    @LocalServerPort
    private int port;

    /**
     * Конфигурация класса перед тестами.
     */
    @BeforeAll
    public void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1/user";
    }

    @Test
    @DisplayName("Тест нахождения пользователя с несуществующим username")
    public void findUserByNotExistingUsernameTest() {
        Response response = findUserByNotExistingUsernameAndGetResponse();

        ApplicationException applicationException = response
            .getBody()
            .as(ApplicationException.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertEquals(BaseErrorCode.NOT_FOUND, applicationException.getErrorCode());
    }

    /**
     * Аутентифицировать пользователя и получить ответ.
     *
     * @return ответ
     */
    private Response findUserByNotExistingUsernameAndGetResponse() {
        return RestAssured
            .given()
            .contentType("application/json")
            .when()
            .get("/username1");
    }
}
