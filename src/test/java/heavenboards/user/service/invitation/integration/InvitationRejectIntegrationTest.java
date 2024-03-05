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
import transfer.contract.api.UserApi;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.invitation.InvitationOperationErrorCode;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.user.UserTo;

import java.util.UUID;

/**
 * Интеграционные тесты отклонения приглашений.
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
public class InvitationRejectIntegrationTest extends BaseInvitationIntegrationTest {
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
     * Тест валидного отклонения существующего приглашения.
     */
    @Test
    @DisplayName("Тест валидного отклонения существующего приглашения")
    public void existingInvitationRejectTest() {
        String invitedUserUsername = "invitedUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);
        securityTestUtil.securityContextHelper(invitedUserTo);

        Mockito.when(userApi.findUserByUsername(invitedUserUsername))
            .thenReturn(invitedUserTo);

        InvitationEntity rejectedInvitation =
            findInvitationById(UUID.fromString("625c0921-e767-4269-a98c-d9ff571bbb8c"));

        Response response = rejectInvitationAndGetResponse(rejectedInvitation.getId());
        InvitationOperationResultTo operationResultTo = response.getBody()
            .as(InvitationOperationResultTo.class);

        Assertions.assertEquals(OperationStatus.OK, operationResultTo.getStatus());
        Assertions.assertEquals(rejectedInvitation.getId(), operationResultTo.getInvitationId());

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
     * Тест отклонения приглашения чужого пользователя.
     */
    @Test
    @DisplayName("Тест отклонения приглашения чужого пользователя")
    public void rejectAnotherUserInvitationTest() {
        String invitationSenderUsername = "invitationSender";
        UserEntity invitationSenderEntity = findUserByUsername(invitationSenderUsername);
        UserTo invitationSenderTo = userMapper.mapFromEntity(invitationSenderEntity);
        securityTestUtil.securityContextHelper(invitationSenderTo);

        Mockito.when(userApi.findUserByUsername(invitationSenderUsername))
            .thenReturn(invitationSenderTo);

        InvitationEntity rejectedInvitation =
            findInvitationById(UUID.fromString("625c0921-e767-4269-a98c-d9ff571bbb8c"));

        Response response = rejectInvitationAndGetResponse(rejectedInvitation.getId());
        InvitationOperationResultTo operationResultTo = response.getBody()
            .as(InvitationOperationResultTo.class);

        Assertions.assertEquals(OperationStatus.FAILED, operationResultTo.getStatus());
        Assertions.assertEquals(1, operationResultTo.getErrors().size());
        Assertions.assertEquals(rejectedInvitation.getId(),
            operationResultTo.getErrors().get(0).getFailedInvitationId());
        Assertions.assertEquals(InvitationOperationErrorCode.THIS_IS_NOT_YOUR_INVITATION,
            operationResultTo.getErrors().get(0).getErrorCode());
    }

    /**
     * Отправить запрос на отклонение приглашения и получить ответ.
     *
     * @param invitationId - идентификатор приглашения, которое отклоняет пользователь
     * @return ответ
     */
    private Response rejectInvitationAndGetResponse(UUID invitationId) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(InvitationTo.builder()
                .id(invitationId)
                .build())
            .when()
            .post("/invitation/reject");
    }
}
