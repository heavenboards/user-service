package heavenboards.user.service.authentication.service;

import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import heavenboards.user.service.user.mapping.UserMapper;
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
     * Репозиторий для пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для пользователей.
     */
    private final UserMapper userMapper;

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

        UserEntity user = userMapper.mapFromRegistrationRequest(request);
        userRepository.save(user);
        return AuthenticationOperationResultTo.builder()
            .userId(user.getId())
            .token(tokenGenerator.generate(user))
            .build();
    }
}
