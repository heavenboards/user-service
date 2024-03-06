package heavenboards.user.service.authentication.service;

import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import heavenboards.user.service.user.mapping.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.service.jwt.JwtTokenGenerator;
import transfer.contract.domain.authentication.AuthenticationOperationErrorCode;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.Optional;

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
     * @param user - данные пользователя для регистрации
     * @return результат операции с токеном
     */
    @Transactional
    public AuthenticationOperationResultTo register(final UserTo user) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return AuthenticationOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(AuthenticationOperationResultTo.AuthenticationOperationErrorTo
                    .builder()
                    .failedUserId(existingUser.get().getId())
                    .errorCode(AuthenticationOperationErrorCode.USERNAME_ALREADY_EXIST)
                    .build()))
                .build();
        }

        UserEntity entity = userMapper.mapForRegistration(user);
        userRepository.save(entity);
        return AuthenticationOperationResultTo.builder()
            .userId(entity.getId())
            .token(tokenGenerator.generate(entity))
            .build();
    }
}
