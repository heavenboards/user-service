package heavenboards.user.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.user.UserRole;

import java.time.ZonedDateTime;

/**
 * Обертка для создания объектов UserEntity.
 */
@Component
@RequiredArgsConstructor
public class UserEntityBuilder {
    /**
     * Класс для шифрования паролей.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Построить объект UserEntity с полями из запроса на регистрацию.
     *
     * @param request - данные из запроса на регистрацию
     * @return объект UserEntity с проставленными полями
     */
    public UserEntity buildFromRequestData(RegistrationRequestTo request) {
        return UserEntity.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.USER)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .build();
    }
}
