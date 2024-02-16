package heavenboards.user.service.user;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import transfer.contract.domain.user.UserTo;

/**
 * Маппер для пользователей.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Маппинг из entity в to.
     *
     * @param entity - entity
     * @return to с проставленными полями
     */
    @SuppressWarnings("unused")
    UserTo map(UserEntity entity);

    /**
     * Маппинг из to в entity.
     *
     * @param to - to
     * @return entity с проставленными полями
     */
    @SuppressWarnings("unused")
    @InheritInverseConfiguration
    UserEntity map(UserTo to);
}
