package heavenboards.user.service.invitation.servce;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.invitation.domain.InvitationRepository;
import heavenboards.user.service.invitation.mapping.InvitationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import transfer.contract.api.ProjectApi;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.invitation.InvitationOperationErrorCode;
import transfer.contract.domain.invitation.InvitationOperationResultTo;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
     * Api-клиент для сервиса проектов.
     */
    private final ProjectApi projectApi;

    /**
     * Запрос на создание приглашения пользователя в проект.
     *
     * @param invitation - to-модель приглашения пользователя в проект
     * @return результат создания приглашения
     */
    public InvitationOperationResultTo createInvitation(
        final @RequestBody InvitationTo invitation
    ) {
        checkProjectExist(invitation.getProject().getId());
        Optional<InvitationEntity> invitationEntity = invitationRepository
            .findByProjectIdAndInvitedUserId(invitation.getProject().getId(),
                invitation.getUser().getId());

        if (invitationEntity.isPresent()) {
            return InvitationOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(InvitationOperationResultTo.InvitationOperationErrorTo.builder()
                    .errorCode(InvitationOperationErrorCode.INVITATION_ALREADY_CREATED)
                    .failedInvitationId(invitationEntity.get().getId())
                    .build()))
                .build();
        }

        InvitationEntity entity = invitationRepository.save(invitationMapper
            .mapFromTo(new InvitationEntity(), invitation));

        return InvitationOperationResultTo.builder()
            .invitationId(entity.getId())
            .build();
    }

    /**
     * Проверить существует ли проект по идентификатору.
     *
     * @param projectId - идентификатор проверяемого проекта
     * @throws ClientApplicationException - если проект по идентификатору не найден
     */
    private void checkProjectExist(final UUID projectId) throws ClientApplicationException {
        try {
            ProjectTo project = projectApi.findProjectById(projectId);
            if (!Objects.equals(project.getId(), projectId)) {
                throw new RuntimeException();
            }
        } catch (Exception ignored) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Проект с идентификатором %s не найден", projectId));
        }
    }
}
