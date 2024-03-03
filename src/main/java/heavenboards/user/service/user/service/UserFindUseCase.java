package heavenboards.user.service.user.service;

import heavenboards.user.service.user.domain.UserRepository;
import heavenboards.user.service.user.mapping.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

/**
 * Use case поиска пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserFindUseCase {
    /**
     * Репозиторий для пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для пользователей.
     */
    private final UserMapper userMapper;

    /**
     * Получение пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь или пустота
     */
    @Transactional(readOnly = true)
    public UserTo findUserByUsername(final String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::mapFromEntity)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с username %s не найден", username)));
    }
}
