package heavenboards.user.service.invitation.servce;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.invitation.domain.InvitationRepository;
import heavenboards.user.service.invitation.mapping.InvitationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;

/**
 * Use case создания приглашения пользователя в проект.
 */
@Service
@RequiredArgsConstructor
public class InvitationCreateUseCase {
    /**
     * Маппер для приглашений.
     */
    private final InvitationMapper invitationMapper;

    /**
     * Репозиторий для приглашений.
     */
    private final InvitationRepository invitationRepository;

    /**
     * Запрос на создание приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат создания приглашения
     */
    public InvitationOperationResultTo createInvitation(
        final @RequestBody InvitationTo invitation
    ) {
        InvitationEntity entity = invitationRepository.save(invitationMapper
            .mapFromTo(new InvitationEntity(), invitation));

        return InvitationOperationResultTo.builder()
            .invitationId(entity.getId())
            .build();
    }
}
