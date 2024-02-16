package heavenboards.user.service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для пользователей.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    /**
     * Поиск пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Существует ли пользователь по username.
     *
     * @param username - username
     * @return true, если существует, иначе false
     */
    boolean existsByUsername(String username);
}
