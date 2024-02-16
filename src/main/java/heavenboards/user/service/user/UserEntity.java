package heavenboards.user.service.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import transfer.contract.domain.user.UserRole;

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
@Entity
@Table(name = "user")
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
    @Enumerated(EnumType.STRING)
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
