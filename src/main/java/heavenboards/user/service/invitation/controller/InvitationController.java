package heavenboards.user.service.invitation.controller;

import heavenboards.user.service.invitation.servce.InvitationAcceptUseCase;
import heavenboards.user.service.invitation.servce.InvitationCreateUseCase;
import heavenboards.user.service.invitation.servce.InvitationFindUseCase;
import heavenboards.user.service.invitation.servce.InvitationRejectUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;

import java.util.List;

/**
 * Контроллер для взаимодействия с приглашениями пользователей в проекты.
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    /**
     * Use case создания приглашения пользователя в проект.
     */
    private final InvitationCreateUseCase invitationCreateUseCase;

    /**
     * Use case поиска приглашений пользователя в проекты.
     */
    private final InvitationFindUseCase invitationFindUseCase;

    /**
     * Use case подтверждения приглашения пользователя в проект.
     */
    private final InvitationAcceptUseCase invitationAcceptUseCase;

    /**
     * Use case отклонения приглашения пользователя в проект.
     */
    private final InvitationRejectUseCase invitationRejectUseCase;

    /**
     * Найти все приглашения, которые пришли этому пользователю в проекты.
     *
     * @return все найденные приглашения
     */
    @GetMapping("/received")
    @Operation(summary = "Запрос на получение всех входящих приглашений пользователя в проекты")
    public List<InvitationTo> findAllReceivedInvitations() {
        return invitationFindUseCase.findAllReceivedInvitations();
    }

    /**
     * Найти все приглашения, которые присылал этот пользователь другим пользователям.
     *
     * @return все найденные приглашения
     */
    @GetMapping("/sent")
    @Operation(summary = "Запрос на получение всех приглашений, "
        + "которые присылал этот пользователь другим пользователям")
    public List<InvitationTo> findAllSentInvitations() {
        return invitationFindUseCase.findAllSentInvitations();
    }

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

    /**
     * Запрос на подтверждение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат подтверждения приглашения
     */
    @PostMapping("/accept")
    @Operation(summary = "Запрос на подтверждение приглашения пользователя в проект")
    public InvitationOperationResultTo acceptInvitation(
        final @Valid @RequestBody InvitationTo invitation
    ) {
        return invitationAcceptUseCase.acceptInvitation(invitation);
    }

    /**
     * Запрос на отклонение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат отклонения приглашения
     */
    @PostMapping("/reject")
    @Operation(summary = "Запрос на отклонение приглашения пользователя в проект")
    public InvitationOperationResultTo rejectInvitation(
        final @Valid @RequestBody InvitationTo invitation
    ) {
        return invitationRejectUseCase.rejectInvitation(invitation);
    }
}
