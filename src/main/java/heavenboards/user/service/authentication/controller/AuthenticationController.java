package heavenboards.user.service.authentication.controller;

import heavenboards.user.service.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.authentication.AuthenticationOperationResultTo;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.authentication.RegistrationRequestTo;

/**
 * Контроллер для регистрации и аутентификации.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    /**
     * Сервис для работы с аутентификацией / регистрацией пользователей.
     */
    private final AuthenticationService authenticationService;

    /**
     * Запрос на регистрацию пользователя.
     *
     * @param request - данные для регистрации
     * @return результат операции с токеном
     */
    @PostMapping("/register")
    public AuthenticationOperationResultTo register(
        final @Valid @RequestBody RegistrationRequestTo request
    ) {
        return authenticationService.register(request);
    }

    /**
     * Запрос на аутентификацию.
     *
     * @param request - данные для аутентификации
     * @return результат операции с токеном
     */
    @PostMapping("/authenticate")
    public AuthenticationOperationResultTo authenticate(
        final @Valid @RequestBody AuthenticationRequestTo request
    ) {
        return authenticationService.authenticate(request);
    }
}
