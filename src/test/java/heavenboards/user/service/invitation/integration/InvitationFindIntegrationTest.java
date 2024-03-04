package heavenboards.user.service.invitation.integration;

import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.mapping.UserMapper;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import security.service.util.test.SecurityTestUtil;
import transfer.contract.api.ProjectApi;
import transfer.contract.api.UserApi;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.UUID;

/**
 * Интеграционные тесты поиска приглашений.
 */
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = {
        "classpath:sql/invitation/create.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class InvitationFindIntegrationTest extends BaseInvitationIntegrationTest {
    /**
     * Utility-класс с настройкой security для тестов.
     */
    @Autowired
    private SecurityTestUtil securityTestUtil;

    /**
     * Mock api-клиента для сервиса пользователей.
     */
    @MockBean
    private UserApi userApi;

    /**
     * Маппер для пользователей.
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * Mock api-клиента для сервиса проектов.
     */
    @MockBean
    private ProjectApi projectApi;

    /**
     * Порт приложения.
     */
    @LocalServerPort
    private int port;

    /**
     * Конфигурация перед тестами.
     */
    @BeforeAll
    public void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1";
        RestAssured.defaultParser = Parser.JSON;
    }

    /**
     * Тест поиска входящих приглашений пользователя в проекты.
     */
    @Test
    @DisplayName("Тест поиска входящих приглашений пользователя в проекты")
    public void findAllReceivedInvitationsTest() {
        String invitedUserUsername = "invitedUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);
        securityTestUtil.securityContextHelper(invitedUserTo);

        Mockito.when(userApi.findUserByUsername(invitedUserUsername))
            .thenReturn(invitedUserTo);
        Mockito.when(projectApi.findProjectById(Mockito.any()))
            .thenReturn(ProjectTo.builder()
                .id(UUID.randomUUID())
                .name("Existing project")
                .build());

        Response response = findReceivedInvitationsAndGetResponse();
        List<InvitationTo> invitations = response
            .jsonPath().getList(".", InvitationTo.class);
        List<UUID> invitationIds = invitations.stream()
            .map(InvitationTo::getId)
            .toList();

        Assertions.assertEquals(2, invitations.size());
        Assertions.assertTrue(invitationIds.contains(UUID
            .fromString("625c0921-e767-4269-a98c-d9ff571bbb8c")));
        Assertions.assertTrue(invitationIds.contains(UUID
            .fromString("b2308466-ee4b-4137-8cd7-6a8226b53525")));

        for (InvitationTo invitation : invitations) {
            Assertions.assertEquals(UUID.fromString("e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795"),
                invitation.getInvitedUser().getId());
            Assertions.assertEquals(UUID.fromString("2baed0f0-49b2-43fa-bec2-c3b4af8b2918"),
                invitation.getInvitationSender().getId());
        }
    }

    /**
     * Тест поиска исходящих приглашений пользователя.
     */
    @Test
    @DisplayName("Тест поиска исходящих приглашений пользователя")
    public void findAllSentInvitationsTest() {
        String invitationSenderUsername = "invitationSender";
        UserEntity invitationSenderEntity = findUserByUsername(invitationSenderUsername);
        UserTo invitationSenderTo = userMapper.mapFromEntity(invitationSenderEntity);
        securityTestUtil.securityContextHelper(invitationSenderTo);

        Mockito.when(userApi.findUserByUsername(invitationSenderUsername))
            .thenReturn(invitationSenderTo);
        Mockito.when(projectApi.findProjectById(Mockito.any()))
            .thenReturn(ProjectTo.builder()
                .id(UUID.randomUUID())
                .name("Existing project")
                .build());

        Response response = findSentInvitationsAndGetResponse();
        List<InvitationTo> invitations = response
            .jsonPath().getList(".", InvitationTo.class);
        List<UUID> invitationIds = invitations.stream()
            .map(InvitationTo::getId)
            .toList();

        Assertions.assertEquals(2, invitations.size());
        Assertions.assertTrue(invitationIds.contains(UUID
            .fromString("625c0921-e767-4269-a98c-d9ff571bbb8c")));
        Assertions.assertTrue(invitationIds.contains(UUID
            .fromString("b2308466-ee4b-4137-8cd7-6a8226b53525")));

        for (InvitationTo invitation : invitations) {
            Assertions.assertEquals(UUID.fromString("e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795"),
                invitation.getInvitedUser().getId());
            Assertions.assertEquals(UUID.fromString("2baed0f0-49b2-43fa-bec2-c3b4af8b2918"),
                invitation.getInvitationSender().getId());
        }
    }

    /**
     * Отправить запрос на получение всех входящих приглашений в проекты.
     *
     * @return ответ
     */
    private Response findReceivedInvitationsAndGetResponse() {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .when()
            .get("/invitation/received");
    }

    /**
     * Отправить запрос на получение всех исходящих приглашений в проекты.
     *
     * @return ответ
     */
    private Response findSentInvitationsAndGetResponse() {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .when()
            .get("/invitation/sent");
    }
}
