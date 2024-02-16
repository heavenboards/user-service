package heavenboards.user.service.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Сущность пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table("user")
public class UserEntity {
    /**
     * Идентификатор.
     */
    @Id
    private UUID id;

    /**
     * Уникальное имя пользователя.
     */
    private String username;

    /**
     * Хэшированный пароль.
     */
    private String password;

    /**
     * Роль.
     */
    private UserRole role;

    /**
     * Имя.
     */
    private String firstName;

    /**
     * Фамилия.
     */
    private String lastName;

    /**
     * Признак "Удален" (для soft-delete).
     */
    private boolean deleted;

    /**
     * Дата и время создания.
     */
    private ZonedDateTime createdAt;

    /**
     * Дата и время последнего обновления.
     */
    private ZonedDateTime updatedAt;

    @ToString.Include
    @SuppressWarnings("unused")
    private String maskPassword() {
        return "********";
    }
}
