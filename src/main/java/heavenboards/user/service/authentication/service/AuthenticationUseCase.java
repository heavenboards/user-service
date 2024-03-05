package heavenboards.user.service.authentication.service;

import heavenboards.user.service.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationOperationErrorCode;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;

/**
 * Use case для аутентификации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationUseCase {
    /**
     * Репозиторий для пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Класс для генерации JWT-токенов.
     */
    private final JwtTokenGenerator tokenGenerator;

    /**
     * AuthenticationManager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Аутентифицировать пользователя.
     *
     * @param request - данные для аутентификации
     * @return результат операции с токеном
     */
    @Transactional(readOnly = true)
    public AuthenticationOperationResultTo authenticate(final AuthenticationRequestTo request) {
        checkUsernameExists(request.getUsername());

        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
            );

            authenticationManager.authenticate(authenticationToken);
            return userRepository.findByUsername(request.getUsername())
                .map(user -> AuthenticationOperationResultTo.builder()
                    .userId(user.getId())
                    .token(tokenGenerator.generate(user))
                    .build())
                .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                    String.format("Пользователь с username %s не найден", request.getUsername())));
        } catch (Exception ignored) {
            return AuthenticationOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                    .builder()
                    .errorCode(AuthenticationOperationErrorCode.INVALID_USERNAME_PASSWORD)
                    .build()))
                .build();
        }
    }

    /**
     * Проверить наличие пользователя по username.
     *
     * @param username - username
     * @throws ClientApplicationException - если username не найден
     */
    private void checkUsernameExists(final String username) throws ClientApplicationException {
        if (!userRepository.existsByUsername(username)) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с username %s не найден", username));
        }
    }
}
