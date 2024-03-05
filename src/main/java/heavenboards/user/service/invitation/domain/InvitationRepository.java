package heavenboards.user.service.invitation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для приглашений.
 */
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {
    /**
     * Поиск приглашения пользователя в проект.
     *
     * @param projectId     - идентификатор проекта
     * @param invitedUserId - идентификатор пользователя
     * @return приглашение этого пользователя в этот проект или пустота
     */
    Optional<InvitationEntity> findByProjectIdAndInvitedUserId(UUID projectId, UUID invitedUserId);

    /**
     * Найти все приглашения, которые пришли этому пользователю в проекты.
     *
     * @param invitedUserId - идентификатор пользователя, приглашения которого в проекты мы ищем
     * @return все входящие приглашения пользователя
     */
    List<InvitationEntity> findAllByInvitedUserId(UUID invitedUserId);

    /**
     * Найти все приглашения, которые пользователь присылал другим пользователям.
     *
     * @param invitationSenderId - идентификатор пользователя, приглашения от которого мы ищем
     * @return все исходящие приглашения пользователя
     */
    List<InvitationEntity> findAllByInvitationSenderId(UUID invitationSenderId);
}
