package heavenboards.user.service.authentication.service;

import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserEntityBuilder;
import heavenboards.user.service.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationOperationErrorCode;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.common.OperationStatus;

import java.util.List;

/**
 * Use case для регистрации пользователей.
 */
@Service
@RequiredArgsConstructor
public class RegistrationUseCase {
    /**
     * Обертка для создания объектов UserEntity.
     */
    private final UserEntityBuilder entityBuilder;

    /**
     * Репозиторий для пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Класс для генерации JWT-токенов.
     */
    private final JwtTokenGenerator tokenGenerator;

    /**
     * Зарегистрировать пользователя.
     *
     * @param request - данные для регистрации
     * @return результат операции с токеном
     */
    public AuthenticationOperationResultTo register(final RegistrationRequestTo request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthenticationOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                    .builder()
                    .errorCode(AuthenticationOperationErrorCode.USERNAME_ALREADY_EXIST)
                    .build()))
                .build();
        }

        UserEntity user = entityBuilder.buildFromRequestData(request);
        userRepository.save(user);
        return AuthenticationOperationResultTo.builder()
            .userId(user.getId())
            .token(tokenGenerator.generate(user))
            .build();
    }
}
