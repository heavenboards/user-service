package heavenboards.user.service.invitation.integration;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.invitation.domain.InvitationRepository;
import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseInvitationIntegrationTest {
    /**
     * Репозиторий для пользователей.
     */
    @Autowired
    protected UserRepository userRepository;

    /**
     * Репозиторий для приглашений.
     */
    @Autowired
    protected InvitationRepository invitationRepository;

    /**
     * Получить сущность пользователя по username.
     *
     * @param username - username
     * @return сущность пользователя или ClientApplicationException
     */
    protected UserEntity findUserByUsername(final String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с username %s не найден", username)));
    }

    /**
     * Получить сущность приглашения по идентификатору.
     *
     * @param invitationId - идентификатор приглашения
     * @return сущность приглашения или ClientApplicationException
     */
    protected InvitationEntity findInvitationById(final UUID invitationId) {
        return invitationRepository.findById(invitationId)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Приглашение с идентификатором %s не найдено", invitationId)));
    }
}
