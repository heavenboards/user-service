package heavenboards.user.service.invitation.integration;

import heavenboards.user.service.invitation.domain.InvitationEntity;
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
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.invitation.InvitationOperationErrorCode;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.UUID;

/**
 * Интеграционные тесты подтверждения приглашений.
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
public class InvitationAcceptIntegrationTest extends BaseInvitationIntegrationTest {
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
     * Mock api-клиента для сервиса проектов.
     */
    @MockBean
    private ProjectApi projectApi;

    /**
     * Маппер для пользователей.
     */
    @Autowired
    private UserMapper userMapper;

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
     * Тест валидного подтверждения существующего приглашения.
     */
    @Test
    @DisplayName("Тест валидного подтверждения существующего приглашения")
    public void validInvitationAcceptTest() {
        String invitedUserUsername = "invitedUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);
        securityTestUtil.securityContextHelper(invitedUserTo);

        ProjectTo project = ProjectTo.builder()
            .id(UUID.fromString("bf9a55de-a3b4-4a7b-8435-8fdb73759cb7"))
            .name("Existing project")
            .build();

        Mockito.when(userApi.findUserByUsername(invitedUserUsername))
            .thenReturn(invitedUserTo);
        Mockito.when(projectApi.findProjectById(project.getId()))
            .thenReturn(project);

        InvitationEntity acceptedInvitation =
            findInvitationById(UUID.fromString("625c0921-e767-4269-a98c-d9ff571bbb8c"));

        Response response = acceptInvitationAndGetResponse(acceptedInvitation.getId(), project);
        InvitationOperationResultTo operationResult = response.getBody()
            .as(InvitationOperationResultTo.class);

        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());
        Mockito.verify(projectApi, Mockito.times(1)).updateProject(project);

        invitedUserEntity = findUserByUsername(invitedUserUsername);
        Assertions.assertEquals(1, invitedUserEntity.getInvitations().size());
        Assertions.assertEquals(UUID.fromString("b2308466-ee4b-4137-8cd7-6a8226b53525"),
            invitedUserEntity.getInvitations().get(0).getId());

        String invitationSenderUsername = "invitationSender";
        UserEntity invitationSender = findUserByUsername(invitationSenderUsername);
        Assertions.assertEquals(1, invitationSender.getSentInvitations().size());
        Assertions.assertEquals(UUID.fromString("b2308466-ee4b-4137-8cd7-6a8226b53525"),
            invitationSender.getSentInvitations().get(0).getId());
    }

    /**
     * Тест подтверждения приглашения чужого пользователя.
     */
    @Test
    @DisplayName("Тест подтверждения приглашения чужого пользователя")
    public void acceptAnotherUserInvitationTest() {
        String invitationSenderUsername = "invitationSender";
        UserEntity invitationSenderEntity = findUserByUsername(invitationSenderUsername);
        UserTo invitationSenderTo = userMapper.mapFromEntity(invitationSenderEntity);
        securityTestUtil.securityContextHelper(invitationSenderTo);

        ProjectTo project = ProjectTo.builder()
            .id(UUID.fromString("bf9a55de-a3b4-4a7b-8435-8fdb73759cb7"))
            .name("Existing project")
            .build();

        Mockito.when(userApi.findUserByUsername(invitationSenderUsername))
            .thenReturn(invitationSenderTo);
        Mockito.when(projectApi.findProjectById(project.getId()))
            .thenReturn(project);

        InvitationEntity acceptedInvitation =
            findInvitationById(UUID.fromString("625c0921-e767-4269-a98c-d9ff571bbb8c"));

        Response response = acceptInvitationAndGetResponse(acceptedInvitation.getId(), project);
        InvitationOperationResultTo operationResult = response.getBody()
            .as(InvitationOperationResultTo.class);

        Assertions.assertEquals(OperationStatus.FAILED, operationResult.getStatus());
        Assertions.assertEquals(List.of(InvitationOperationResultTo.InvitationOperationErrorTo
            .builder()
            .failedInvitationId(acceptedInvitation.getId())
            .errorCode(InvitationOperationErrorCode.THIS_IS_NOT_YOUR_INVITATION)
            .build()), operationResult.getErrors());
    }

    /**
     * Отправить запрос на подтверждение приглашения и получить ответ.
     *
     * @param invitationId - идентификатор приглашения, которое подтверждает пользователь
     * @param project      - проект, в который приглашается пользователь
     * @return ответ
     */
    private Response acceptInvitationAndGetResponse(UUID invitationId, ProjectTo project) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(InvitationTo.builder()
                .id(invitationId)
                .project(project)
                .build())
            .when()
            .post("/invitation/accept");
    }
}
