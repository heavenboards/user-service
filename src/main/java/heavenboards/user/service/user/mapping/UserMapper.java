package heavenboards.user.service.user.mapping;

import heavenboards.user.service.user.domain.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import transfer.contract.domain.authentication.RegistrationRequestTo;
import transfer.contract.domain.user.UserTo;

/**
 * Маппер для пользователей.
 */
@Getter
@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class UserMapper {
    /**
     * Бин для кодирования пароля.
     */
    private PasswordEncoder encoder;

    /**
     * Маппинг из entity в to.
     *
     * @param entity - сущность
     * @return to с проставленными полями
     */
    @Mapping(target = "projects", ignore = true)
    public abstract UserTo mapFromEntity(UserEntity entity);

    /**
     * Преобразование из запроса на регистрацию в entity.
     *
     * @param to - запрос на регистрацию пользователя
     * @return сущность UserEntity с проставленными полями
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(getEncoder().encode(to.getPassword()))")
    @Mapping(target = "createdAt", expression = "java(java.time.ZonedDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.ZonedDateTime.now())")
    public abstract UserEntity mapFromRegistrationRequest(RegistrationRequestTo to);

    /**
     * Внедрение бина для кодирования пароля.
     *
     * @param passwordEncoder - бин PasswordEncoder
     */
    @Autowired
    public void setEncoder(final PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }
}
