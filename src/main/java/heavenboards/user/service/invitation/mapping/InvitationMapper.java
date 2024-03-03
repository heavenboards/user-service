package heavenboards.user.service.invitation.mapping;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import lombok.Getter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.UUID;

/**
 * Маппер для приглашений.
 */
@Getter
@Mapper(componentModel = "spring")
public abstract class InvitationMapper {
    /**
     * Репозиторий для пользователей.
     */
    private UserRepository userRepository;

    /**
     * Маппинг из to в entity.
     *
     * @param invitation - сущность, которой проставляем поля
     * @param to     - to-модель приглашения
     * @return сущность с проставленными полями
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invitedUser", ignore = true)
    @Mapping(target = "invitationSender", ignore = true)
    @Mapping(target = "projectId", source = "project.id")
    public abstract InvitationEntity mapFromTo(@MappingTarget InvitationEntity invitation,
                                               InvitationTo to);

    /**
     * После маппинга из to в entity - проставляем user.
     *
     * @param invitation - сущность, которой проставляем поля
     * @param to     - to-модель приглашения
     */
    @AfterMapping
    @SuppressWarnings("unused")
    public void afterMappingFromTo(final @MappingTarget InvitationEntity invitation,
                                   final InvitationTo to) {
        var invitationSender = (UserTo) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        // Пользователь, которого мы приглашаем
        UUID invitedUserId = to.getUser().getId();
        UserEntity invitedUser = userRepository.findById(invitedUserId)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с идентификатором %s не найден", invitedUserId)));

        // Пользователь, который отправляет приглашение
        UUID invitationSenderId = invitationSender.getId();
        UserEntity invitationSenderEntity = userRepository.findById(invitationSenderId)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с идентификатором %s не найден", invitationSenderId)));

        invitation.setInvitedUser(invitedUser);
        invitation.setInvitationSender(invitationSenderEntity);
        invitedUser.getInvitations().add(invitation);
        invitationSenderEntity.getSentInvitations().add(invitation);
    }

    /**
     * Внедрение бина репозитория для пользователей.
     *
     * @param repository - бин UserRepository
     */
    @Autowired
    public void setUserRepository(final UserRepository repository) {
        this.userRepository = repository;
    }
}
