package heavenboards.user.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public UserEntity findUserById(@PathVariable String username) {
        return userService.findUserById(username)
            .orElseThrow(RuntimeException::new);
    }
}
