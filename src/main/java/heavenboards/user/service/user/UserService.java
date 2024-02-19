package heavenboards.user.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {
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
    public Optional<UserTo> findUserByUsername(final String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::mapFromEntity);
    }

    /**
     * Найти всех пользователей по идентификаторам.
     *
     * @param ids - идентификаторы
     * @return пользователи
     */
    public List<UserTo> findAllByIds(final Set<UUID> ids) {
        return userRepository.findAllByIdIn(ids).stream()
            .map(userMapper::mapFromEntity)
            .toList();
    }
}
