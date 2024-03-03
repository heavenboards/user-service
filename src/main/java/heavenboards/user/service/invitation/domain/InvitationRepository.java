package heavenboards.user.service.invitation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Репозиторий для приглашений.
 */
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {
}
