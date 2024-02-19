package heavenboards.user.service.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import transfer.contract.domain.user.UserTo;

/**
 * Маппер для пользователей.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Маппинг из entity в to.
     *
     * @param entity - сущность
     * @return to с проставленными полями
     */
    @Mapping(target = "projects", ignore = true)
    UserTo mapFromEntity(UserEntity entity);
}
