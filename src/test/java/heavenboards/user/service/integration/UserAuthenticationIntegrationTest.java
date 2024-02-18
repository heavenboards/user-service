package heavenboards.user.service.integration;

import heavenboards.user.service.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import security.service.jwt.JwtTokenExtractor;
import transfer.contract.api.UserApi;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.authentication.TokenResponseTo;
import transfer.contract.domain.error.ServerErrorCode;
import transfer.contract.domain.error.ServerErrorTo;
import transfer.contract.exception.ServerException;

/**
 * Интеграционные тесты для аутентификации пользователей.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = {
        "classpath:sql/user/create.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class UserAuthenticationIntegrationTest {
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
     * Репозиторий для пользователей.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Mock api-клиента для сервиса пользователей.
     */
    @MockBean
    private UserApi userApi;

    /**
     * Конфигурация класса перед тестами.
     */
    @BeforeAll
    public void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1/auth";
    }

    /**
     * Тест валидной аутентификации.
     */
    @Test
    @DisplayName("Тест валидной аутентификации")
    public void validAuthenticationTest() {
        Mockito.when(userApi.findUserByUsername("username"))
            .thenReturn(userRepository.findByUsername("username")
                .orElseThrow(() -> ServerException.of(ServerErrorCode.USERNAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND)));

        String username = "username";
        Response response = authenticateUserAndGetResponse(username);

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
     * Тест аутентификации с несуществующим username.
     */
    @Test
    @DisplayName("Тест аутентификации с несуществующим username")
    public void notExistingUsernameAuthenticationTest() {
        String username = "username1";
        Response response = authenticateUserAndGetResponse(username);

        ServerErrorTo serverErrorTo = response
            .getBody()
            .as(ServerErrorTo.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
        Assertions.assertNotNull(serverErrorTo);
        Assertions.assertNotNull(serverErrorTo.getErrorCode());
        Assertions.assertEquals(ServerErrorCode.INVALID_USERNAME_PASSWORD, serverErrorTo
            .getErrorCode());
    }

    /**
     * Тест аутентификации с неправильным паролем.
     */
    @Test
    @DisplayName("Тест аутентификации с неправильным паролем")
    public void incorrectPasswordAuthenticationTest() {
        String username = "username";
        Response response = authenticateUserAndGetResponse(username);

        ServerErrorTo serverErrorTo = response
            .getBody()
            .as(ServerErrorTo.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
        Assertions.assertNotNull(serverErrorTo);
        Assertions.assertNotNull(serverErrorTo.getErrorCode());
        Assertions.assertEquals(ServerErrorCode.INVALID_USERNAME_PASSWORD, serverErrorTo
            .getErrorCode());
    }

    /**
     * Аутентифицировать пользователя и получить ответ.
     *
     * @param username - имя пользователя
     * @return ответ
     */
    private Response authenticateUserAndGetResponse(String username) {
        AuthenticationRequestTo body = AuthenticationRequestTo.builder()
            .username(username)
            .password("pAssw0rd123!")
            .build();

        return RestAssured
            .given()
            .contentType("application/json")
            .body(body)
            .when()
            .post("/authenticate");
    }
}
