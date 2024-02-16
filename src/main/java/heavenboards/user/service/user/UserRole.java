package heavenboards.user.service.user;

import lombok.AllArgsConstructor;

/**
 * Роль пользователя.
 */
@AllArgsConstructor
public enum UserRole {
    /**
     * Пользователь.
     */
    USER("Пользователь"),

    /**
     * Администратор.
     */
    ADMIN("Администратор");

    /**
     * Текстовое название роли.
     */
    @SuppressWarnings("unused")
    private final String name;
}
