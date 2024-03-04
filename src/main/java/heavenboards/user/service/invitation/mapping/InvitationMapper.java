package heavenboards.user.service.invitation.mapping;

import heavenboards.user.service.invitation.domain.InvitationEntity;
import heavenboards.user.service.user.domain.UserEntity;
import heavenboards.user.service.user.domain.UserRepository;
import heavenboards.user.service.user.mapping.UserMapper;
import lombok.Getter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import transfer.contract.api.ProjectApi;
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
     * Api-клиент для сервиса проектов.
     */
    private ProjectApi projectApi;

    /**
     * Маппер для пользователей.
     */
    private UserMapper userMapper;

    @Mapping(target = "invitedUser", ignore = true)
    @Mapping(target = "invitationSender", ignore = true)
    public abstract InvitationTo mapFromEntity(@MappingTarget InvitationTo to,
                                               InvitationEntity entity);

    /**
     * Маппинг из to в entity.
     *
     * @param invitation - сущность, которой проставляем поля
     * @param to         - to-модель приглашения
     * @return сущность с проставленными полями
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invitedUser", ignore = true)
    @Mapping(target = "invitationSender", ignore = true)
    @Mapping(target = "projectId", source = "project.id")
    public abstract InvitationEntity mapFromTo(@MappingTarget InvitationEntity invitation,
                                               InvitationTo to);

    /**
     * После маппинга из entity в to - проставляем пользователей.
     *
     * @param to     - to-модель приглашения, которой мы проставляем поля
     * @param entity - сущность приглашения
     */
    @AfterMapping
    @SuppressWarnings("unused")
    public void afterMappingFromEntity(final @MappingTarget InvitationTo to,
                                       final InvitationEntity entity) {
        to.setProject(projectApi.findProjectById(entity.getProjectId()));
        to.setInvitedUser(userMapper.mapFromEntity(entity.getInvitedUser()));
        to.setInvitationSender(userMapper.mapFromEntity(entity.getInvitationSender()));
    }

    /**
     * После маппинга из to в entity - проставляем пользователей.
     *
     * @param invitation - сущность, которой проставляем поля
     * @param to         - to-модель приглашения
     */
    @AfterMapping
    @SuppressWarnings("unused")
    public void afterMappingFromTo(final @MappingTarget InvitationEntity invitation,
                                   final InvitationTo to) {
        var invitationSender = (UserTo) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        // Пользователь, которого мы приглашаем
        UUID invitedUserId = to.getInvitedUser().getId();
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

    /**
     * Внедрение бина api-клиента для сервиса проектов
     *
     * @param api - бин ProjectApi
     */
    @Autowired
    public void setProjectApi(ProjectApi api) {
        this.projectApi = api;
    }

    /**
     * Внедрение бина маппера для пользователей.
     *
     * @param mapper - бин UserMapper
     */
    @Autowired
    public void setUserMapper(UserMapper mapper) {
        this.userMapper = mapper;
    }
}
