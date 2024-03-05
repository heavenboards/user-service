package heavenboards.user.service.invitation.servce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;

/**
 * Use case отклонения приглашения пользователя в проект.
 */
@Service
@RequiredArgsConstructor
public class InvitationRejectUseCase {
    /**
     * Запрос на отклонение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат отклонения приглашения
     */
    public InvitationOperationResultTo rejectInvitation(final InvitationTo invitation) {
        return InvitationOperationResultTo.builder().build();
    }
}
