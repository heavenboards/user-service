package heavenboards.user.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
     * Получение пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь или пустота
     */
    public Optional<UserEntity> findUserByUsername(final String username) {
        return userRepository.findByUsername(username);
    }
}
