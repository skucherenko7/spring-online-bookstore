package mate.academy.spring.online.bookstore.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.mapper.UserMapper;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("This email already exists");
        }
        User user = userMapper.requestDtoToUser(requestDto);
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }
}
