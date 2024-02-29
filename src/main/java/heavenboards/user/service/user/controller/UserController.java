package heavenboards.user.service.user.controller;

import heavenboards.user.service.user.service.UserFindUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    public UserTo findUserByUsername(final @PathVariable String username) {
        return userFindUseCase.findUserByUsername(username);
    }

    /**
     * Найти всех пользователей по идентификаторам.
     *
     * @param ids - идентификаторы
     * @return пользователи
     */
    @PostMapping
    public List<UserTo> findUsersByIds(final @RequestBody Set<UUID> ids) {
        return userFindUseCase.findUsersByIds(ids);
    }
}
