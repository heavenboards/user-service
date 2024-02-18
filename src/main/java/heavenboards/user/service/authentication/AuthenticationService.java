package heavenboards.user.service.authentication;

import heavenboards.user.service.user.UserEntity;
import heavenboards.user.service.user.UserEntityBuilder;
import heavenboards.user.service.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.authentication.TokenResponseTo;
import transfer.contract.domain.error.ServerErrorCode;
import transfer.contract.exception.ServerException;

/**
 * Сервис для работы с аутентификацией / регистрацией пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    /**
     * Репозиторий для пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Обертка для создания объектов UserEntity.
     */
    private final UserEntityBuilder entityBuilder;

    /**
     * Класс для генерации JWT-токенов.
     */
    private final JwtTokenGenerator tokenGenerator;

    /**
     * AuthenticationManager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Зарегистрировать пользователя.
     *
     * @param request - данные для регистрации
     * @return токен
     */
    public TokenResponseTo register(final RegistrationRequestTo request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw ServerException.of(ServerErrorCode.USERNAME_ALREADY_EXIST,
                HttpStatus.BAD_REQUEST);
        }

        UserEntity user = entityBuilder.buildFromRequestData(request);
        userRepository.save(user);
        return TokenResponseTo.of(tokenGenerator.generate(user));
    }

    /**
     * Аутентифицировать пользователя.
     *
     * @param request - данные для аутентификации
     * @return токен
     */
    public TokenResponseTo authenticate(final AuthenticationRequestTo request) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
            );

            authenticationManager.authenticate(authenticationToken);
            return userRepository.findByUsername(request.getUsername())
                .map(user -> TokenResponseTo.of(tokenGenerator.generate(user)))
                .orElseThrow(() -> ServerException.of(ServerErrorCode.USERNAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND));
        } catch (Exception ignored) {
            throw ServerException.of(ServerErrorCode.INVALID_USERNAME_PASSWORD,
                HttpStatus.UNAUTHORIZED);
        }
    }
}
