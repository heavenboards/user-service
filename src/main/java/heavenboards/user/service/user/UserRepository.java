package heavenboards.user.service.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Реактивный репозиторий для пользователей.
 */
public interface UserRepository extends R2dbcRepository<UserEntity, UUID> {
    /**
     * Поиск пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь
     */
    Mono<UserEntity> findByUsername(String username);
}
