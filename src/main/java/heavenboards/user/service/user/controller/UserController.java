package heavenboards.user.service.user.controller;

import heavenboards.user.service.user.service.UserFindUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.user.UserTo;

/**
 * Контроллер для взаимодействия с пользователями.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    /**
     * Use case поиска пользователей.
     */
    private final UserFindUseCase userFindUseCase;

    /**
     * Получение пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь
     */
    @GetMapping("/{username}")
    @Operation(summary = "Получение пользователя по username")
    public UserTo findUserByUsername(final @PathVariable String username) {
        return userFindUseCase.findUserByUsername(username);
    }
}
