package heavenboards.user.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.error.ServerErrorCode;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.ServerException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    /**
     * Сервис для пользователей.
     */
    private final UserService userService;

    /**
     * Получение пользователя по username.
     *
     * @param username - username
     * @return найденный пользователь
     */
    @GetMapping("/{username}")
    public UserTo findUserByUsername(final @PathVariable String username) {
        return userService.findUserByUsername(username)
            .orElseThrow(() -> ServerException.of(ServerErrorCode.USERNAME_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    /**
     * Найти всех пользователей по идентификаторам.
     *
     * @param ids - идентификаторы
     * @return пользователи
     */
    @GetMapping
    public List<UserTo> findAllByIds(final @RequestBody Set<UUID> ids) {
        return userService.findAllByIds(ids);
    }
}
