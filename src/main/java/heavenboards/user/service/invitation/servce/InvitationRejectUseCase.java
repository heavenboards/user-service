package heavenboards.user.service.invitation.servce;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.invitation.domain.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.invitation.InvitationOperationErrorCode;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case отклонения приглашения пользователя в проект.
 */
@Service
@RequiredArgsConstructor
public class InvitationRejectUseCase {
    /**
     * Репозиторий для приглашений.
     */
    private final InvitationRepository invitationRepository;

    /**
     * Запрос на отклонение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат отклонения приглашения
     */
    @Transactional
    @SuppressWarnings("Duplicates")
    public InvitationOperationResultTo rejectInvitation(final InvitationTo invitation) {
        Optional<InvitationEntity> invitationEntity =
            invitationRepository.findById(invitation.getId());
        if (invitationEntity.isEmpty()) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Приглашение с идентификатором %s не найдено", invitation.getId()));
        }

        var user = (UserTo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID invitedUserId = invitationEntity.get().getInvitedUser().getId();
        if (!Objects.equals(invitedUserId, user.getId())) {
            return InvitationOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(InvitationOperationResultTo.InvitationOperationErrorTo.builder()
                    .failedInvitationId(invitation.getId())
                    .errorCode(InvitationOperationErrorCode.THIS_IS_NOT_YOUR_INVITATION)
                    .build()))
                .build();
        }

        invitationRepository.delete(invitationEntity.get());
        return InvitationOperationResultTo.builder()
            .invitationId(invitation.getId())
            .build();
    }
}
