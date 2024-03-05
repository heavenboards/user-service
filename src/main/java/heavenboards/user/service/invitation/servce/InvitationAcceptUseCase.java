package heavenboards.user.service.invitation.servce;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.invitation.domain.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transfer.contract.api.ProjectApi;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.invitation.InvitationOperationErrorCode;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case подтверждения приглашения пользователя в проект.
 */
@Service
@RequiredArgsConstructor
public class InvitationAcceptUseCase {
    /**
     * Репозиторий для приглашений.
     */
    private final InvitationRepository invitationRepository;

    /**
     * Api-клиент для сервиса проектов.
     */
    private final ProjectApi projectApi;

    /**
     * Запрос на подтверждение приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат подтверждения приглашения
     */
    @Transactional
    @SuppressWarnings("Duplicates")
    public InvitationOperationResultTo acceptInvitation(final InvitationTo invitation) {
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

        ProjectTo project = getProjectByIdWithCheck(invitation.getProject().getId());
        invitationRepository.delete(invitationEntity.get());

        project.getUsers().add(user);
        projectApi.updateProject(project);
        return InvitationOperationResultTo.builder().build();
    }

    /**
     * Проверить существование проекта по идентификатору и получить проект.
     *
     * @param projectId - идентификатор проекта
     * @return проект по идентификатору
     */
    private ProjectTo getProjectByIdWithCheck(final UUID projectId) {
        try {
            ProjectTo project = projectApi.findProjectById(projectId);
            if (!Objects.equals(project.getId(), projectId)) {
                throw new RuntimeException();
            }

            return project;
        } catch (Exception ignored) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Проект с идентификатором %s не найден", projectId));
        }
    }
}
