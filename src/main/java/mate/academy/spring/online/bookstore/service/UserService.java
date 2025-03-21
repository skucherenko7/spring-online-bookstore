package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
