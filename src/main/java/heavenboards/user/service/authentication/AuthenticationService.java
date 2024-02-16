package heavenboards.user.service.authentication;

import heavenboards.user.service.user.UserEntity;
import heavenboards.user.service.user.UserEntityBuilder;
import heavenboards.user.service.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.authentication.TokenResponseTo;

import java.util.Optional;

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
     * @return токен или пустота (при ошибке)
     */
    public Optional<TokenResponseTo> register(RegistrationRequestTo request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return Optional.empty();
        }

        UserEntity user = entityBuilder.buildFromRequestData(request);
        userRepository.save(user);
        return Optional.of(TokenResponseTo.builder()
            .token(tokenGenerator.generate(user))
            .build());
    }

    /**
     * Аутентифицировать пользователя.
     *
     * @param request - данные для аутентификации
     * @return токен
     */
    public Optional<TokenResponseTo> authenticate(AuthenticationRequestTo request) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getUsername(),
                request.getPassword());
        authenticationManager.authenticate(authenticationToken);
        return userRepository.findByUsername(request.getUsername())
            .flatMap(user -> Optional.of(TokenResponseTo.builder()
                .token(tokenGenerator.generate(user))
                .build()));
    }
}
