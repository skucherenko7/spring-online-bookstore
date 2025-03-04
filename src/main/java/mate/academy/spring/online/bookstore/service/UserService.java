package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
