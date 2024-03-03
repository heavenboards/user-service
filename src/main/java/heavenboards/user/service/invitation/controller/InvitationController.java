package heavenboards.user.service.invitation.controller;

import heavenboards.user.service.invitation.servce.InvitationCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;

/**
 * Контроллер для взаимодействия с приглашениями пользователей в проекты.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    /**
     * Use case создания приглашения пользователя в проект.
     */
    private final InvitationCreateUseCase invitationCreateUseCase;

    /**
     * Запрос на создание приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат создания приглашения
     */
    @PostMapping
    @Operation(summary = "Запрос на создание приглашения пользователя в проект")
    public InvitationOperationResultTo createInvitation(
        final @Valid @RequestBody InvitationTo invitation
    ) {
        return invitationCreateUseCase.createInvitation(invitation);
    }
}
