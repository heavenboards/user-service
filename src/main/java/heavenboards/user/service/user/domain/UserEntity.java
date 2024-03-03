package heavenboards.user.service.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import heavenboards.user.service.invitation.domain.InvitationEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import transfer.contract.domain.user.UserRole;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Сущность пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "user_entity")
public final class UserEntity implements UserDetails {
    /**
     * Идентификатор.
     */
    @Id
    @UuidGenerator
    private UUID id;

    /**
     * Уникальное имя пользователя.
     */
    private String username;

    /**
     * Хэшированный пароль.
     */
    private String password;

    /**
     * Роль.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * Приглашения, которые пришли этому пользователю.
     */
    @OneToMany(mappedBy = "invitedUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<InvitationEntity> invitations = new ArrayList<>();

    /**
     * Приглашения, которые отправил этот пользователь.
     */
    @OneToMany(mappedBy = "invitationSender", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<InvitationEntity> sentInvitations = new ArrayList<>();

    /**
     * Имя.
     */
    private String firstName;

    /**
     * Фамилия.
     */
    private String lastName;

    /**
     * Истекло ли время действия аккаунта.
     */
    @Builder.Default
    private boolean accountNonExpired = true;

    /**
     * Заблокирован ли аккаунт.
     */
    @Builder.Default
    private boolean accountNonLocked = true;

    /**
     * Истекло ли время жизни credentials.
     */
    @Builder.Default
    private boolean credentialsNonExpired = true;

    /**
     * Активен ли аккаунт.
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * Дата и время создания.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createdAt;

    /**
     * Дата и время последнего обновления.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime updatedAt;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
