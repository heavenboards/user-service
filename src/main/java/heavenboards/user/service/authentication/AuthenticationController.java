package heavenboards.user.service.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.authentication.AuthenticationRequestTo;
import transfer.contract.domain.authentication.TokenResponseTo;
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
     * Запрос на регистрацию.
     *
     * @param request - данные для регистрации
     * @return токен
     */
    @PostMapping("/register")
    public TokenResponseTo register(@RequestBody RegistrationRequestTo request) {
        return authenticationService.register(request)
            .orElseThrow(RuntimeException::new);
    }

    /**
     * Запрос на аутентификацию.
     *
     * @param request - данные для аутентификации
     * @return токен
     */
    @PostMapping("/authenticate")
    public TokenResponseTo authenticate(@RequestBody AuthenticationRequestTo request) {
        return authenticationService.authenticate(request)
            .orElseThrow(RuntimeException::new);
    }
}
