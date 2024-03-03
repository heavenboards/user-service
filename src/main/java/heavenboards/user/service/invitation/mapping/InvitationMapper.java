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
import transfer.contract.domain.invitation.InvitationTo;
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
     * @param entity - сущность, которой проставляем поля
     * @param to     - to-модель приглашения
     * @return сущность с проставленными полями
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "projectId", source = "project.id")
    public abstract InvitationEntity mapFromTo(@MappingTarget InvitationEntity entity,
                                               InvitationTo to);

    /**
     * После маппинга из to в entity - проставляем user.
     *
     * @param entity - сущность, которой проставляем поля
     * @param to     - to-модель приглашения
     */
    @AfterMapping
    @SuppressWarnings("unused")
    public void afterMappingFromTo(final @MappingTarget InvitationEntity entity,
                                   final InvitationTo to) {
        UUID userId = to.getUser().getId();
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Пользователь с идентификатором %s не найден", userId)));
        entity.setUser(user);
        user.getInvitations().add(entity);
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
