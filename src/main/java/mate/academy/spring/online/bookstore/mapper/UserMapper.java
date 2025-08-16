package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto userToUserDto(User user);

    User requestDtoToUser(UserRegistrationRequestDto requestDto);
}
