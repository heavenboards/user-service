package heavenboards.user.service.authentication.service;

import heavenboards.user.service.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationOperationErrorCode;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.common.OperationStatus;

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
    public AuthenticationOperationResultTo authenticate(final AuthenticationRequestTo request) {
        try {
            if (!userRepository.existsByUsername(request.getUsername())) {
                return AuthenticationOperationResultTo.builder()
                    .status(OperationStatus.FAILED)
                    .errors(List.of(AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                        .builder()
                        .errorCode(AuthenticationOperationErrorCode.USERNAME_NOT_FOUND)
                        .build()))
                    .build();
            }

            var authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
            );

            authenticationManager.authenticate(authenticationToken);
            return userRepository.findByUsername(request.getUsername())
                .map(user -> AuthenticationOperationResultTo.builder()
                    .userId(user.getId())
                    .token(tokenGenerator.generate(user))
                    .build())
                .orElse(AuthenticationOperationResultTo.builder()
                    .status(OperationStatus.FAILED)
                    .errors(List.of(AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                        .builder()
                        .errorCode(AuthenticationOperationErrorCode.USERNAME_NOT_FOUND)
                        .build()))
                    .build());
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
}