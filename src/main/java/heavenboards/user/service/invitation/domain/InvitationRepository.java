package heavenboards.user.service.invitation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для приглашений.
 */
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {
    /**
     * Поиск приглашения пользователя в проект.
     *
     * @param projectId - идентификатор проекта
     * @param invitedUserId    - идентификатор пользователя
     * @return приглашение этого пользователя в этот проект или пустота
     */
    Optional<InvitationEntity> findByProjectIdAndInvitedUserId(UUID projectId, UUID invitedUserId);
}
