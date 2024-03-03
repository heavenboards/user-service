package heavenboards.user.service.invitation.domain;

import heavenboards.user.service.user.domain.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Сущность приглашения пользователя в проект.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "invitation_entity")
public class InvitationEntity {
    /**
     * Идентификатор.
     */
    @Id
    @UuidGenerator
    private UUID id;

    /**
     * Пользователь, которого приглашают в проект.
     */
    @ManyToOne
    @JoinColumn(name = "invited_user_id", referencedColumnName = "id")
    private UserEntity invitedUser;

    /**
     * Пользователь, который отправил приглашение в проект.
     */
    @ManyToOne
    @JoinColumn(name = "invitation_sender", referencedColumnName = "id")
    private UserEntity invitationSender;

    /**
     * Идентификатор проекта, в который приглашается пользователь.
     */
    private UUID projectId;
}
