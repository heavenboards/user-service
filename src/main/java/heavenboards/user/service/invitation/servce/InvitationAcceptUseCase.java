package heavenboards.user.service.invitation.servce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;

/**
 * Use case подтверждения приглашения пользователя в проект.
 */
@Service
@RequiredArgsConstructor
public class InvitationAcceptUseCase {
    /**
     * Запрос на подтверждение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат подтверждения приглашения
     */
    public InvitationOperationResultTo acceptInvitation(final InvitationTo invitation) {
        return InvitationOperationResultTo.builder().build();
    }
}
