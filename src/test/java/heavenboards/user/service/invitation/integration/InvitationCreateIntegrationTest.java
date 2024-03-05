package heavenboards.user.service.invitation.integration;

import feign.FeignException;
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
import org.junit.jupiter.api.BeforeEach;
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
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интеграционные тесты для создания приглашений.
 */
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
public class InvitationCreateIntegrationTest extends BaseInvitationIntegrationTest {
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

    @BeforeEach
    public void setup() {
        securityTestUtil.securityContextHelper();
        UserTo invitationSender = securityTestUtil.getAuthenticatedUser();
        UserEntity invitationSenderEntity = userRepository.save(userMapper.mapFromTo(invitationSender));
        invitationSender.setId(invitationSenderEntity.getId());
        Mockito.when(userApi.findUserByUsername(invitationSender.getUsername()))
            .thenReturn(invitationSender);
    }

    /**
     * Тест валидного создания приглашения пользователя в проект.
     */
    @Test
    @DisplayName("Тест валидного создания приглашения пользователя в проект")
    public void validInvitationCreateTest() {
        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Existing project")
            .build();
        Mockito.when(projectApi.findProjectById(project.getId()))
            .thenReturn(project);

        // Пользователь, которого приглашают в проект
        String invitedUserUsername = "registeredUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);

        Response response = createInvitationAndGetResponse(invitedUserTo, project);
        InvitationOperationResultTo operationResult = response.getBody()
            .as(InvitationOperationResultTo.class);
        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());

        Optional<InvitationEntity> invitation = invitationRepository
            .findById(operationResult.getInvitationId());
        Assertions.assertTrue(invitation.isPresent());

        String invitationSenderUsername = securityTestUtil.getAuthenticatedUser().getUsername();
        UserEntity invitationSender = findUserByUsername(invitationSenderUsername);

        Assertions.assertEquals(invitationSender.getId(),
            invitation.get().getInvitationSender().getId());
        Assertions.assertEquals(invitedUserEntity.getId(),
            invitation.get().getInvitedUser().getId());
        Assertions.assertEquals(1, invitationSender.getSentInvitations().size());
        Assertions.assertEquals(invitation.get().getId(),
            invitationSender.getSentInvitations().get(0).getId());

        invitedUserEntity = findUserByUsername(invitedUserUsername);
        Assertions.assertEquals(1, invitedUserEntity.getInvitations().size());
        Assertions.assertEquals(invitation.get().getId(),
            invitedUserEntity.getInvitations().get(0).getId());
    }

    /**
     * Тест создания приглашения пользователя в несуществующий проект.
     */
    @Test
    @DisplayName("Тест создания приглашения пользователя в несуществующий проект")
    public void notExistingProjectInvitationCreateTest() {
        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Not existing project")
            .build();
        Mockito.when(projectApi.findProjectById(project.getId()))
            .thenThrow(FeignException.FeignClientException.class);

        // Пользователь, которого приглашают в проект
        String invitedUserUsername = "registeredUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);

        Response response = createInvitationAndGetResponse(invitedUserTo, project);
        ClientApplicationException applicationException = response.getBody()
            .as(ClientApplicationException.class);
        Assertions.assertEquals(BaseErrorCode.NOT_FOUND, applicationException.getErrorCode());
    }

    /**
     * Тест создания дубликата приглашения пользователя в проект.
     */
    @Test
    @DisplayName("Тест создания дубликата приглашения пользователя в проект")
    public void duplicateInvitationCreateTest() {
        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Existing project")
            .build();
        Mockito.when(projectApi.findProjectById(project.getId()))
            .thenReturn(project);

        // Пользователь, которого приглашают в проект
        String invitedUserUsername = "registeredUser";
        UserEntity invitedUserEntity = findUserByUsername(invitedUserUsername);
        UserTo invitedUserTo = userMapper.mapFromEntity(invitedUserEntity);

        Response successResponse = createInvitationAndGetResponse(invitedUserTo, project);
        InvitationOperationResultTo successOperationResult = successResponse.getBody()
            .as(InvitationOperationResultTo.class);
        Assertions.assertEquals(OperationStatus.OK, successOperationResult.getStatus());

        Response errorResponse = createInvitationAndGetResponse(invitedUserTo, project);
        InvitationOperationResultTo errorOperationResult = errorResponse.getBody()
            .as(InvitationOperationResultTo.class);
        Assertions.assertEquals(OperationStatus.FAILED, errorOperationResult.getStatus());
        Assertions.assertEquals(List.of(InvitationOperationResultTo.InvitationOperationErrorTo
            .builder()
            .errorCode(InvitationOperationErrorCode.INVITATION_ALREADY_CREATED)
            .failedInvitationId(successOperationResult.getInvitationId())
            .build()), errorOperationResult.getErrors());
    }

    /**
     * Отправить запрос на создание приглашения и получить ответ.
     *
     * @param invitedUser - to-модель пользователя, которого мы приглашаем в проект
     * @param project     - to-модель проекта, в который мы приглашаем пользователя
     * @return ответ
     */
    private Response createInvitationAndGetResponse(final UserTo invitedUser,
                                                    final ProjectTo project) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(InvitationTo.builder()
                .invitedUser(invitedUser)
                .project(project)
                .build())
            .when()
            .post("/invitation");
    }
}
