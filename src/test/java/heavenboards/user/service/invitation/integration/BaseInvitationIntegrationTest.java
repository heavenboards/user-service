package heavenboards.user.service.invitation.integration;

import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseInvitationIntegrationTest {
    /**
     * Репозиторий для пользователей.
     */
    @Autowired
    protected UserRepository userRepository;

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
}
