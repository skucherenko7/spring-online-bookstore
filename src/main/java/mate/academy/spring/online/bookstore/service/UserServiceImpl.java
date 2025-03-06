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
        System.out.println("Checking if email exists: " + requestDto.getEmail());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            System.out.println("Email already exists: " + requestDto.getEmail());
            throw new RegistrationException("Email already exists");
        }
        System.out.println("Email is unique. Proceeding "
                + "with saving the user: " + requestDto.getEmail());

        User user = userMapper.requestDtoToUser(requestDto);
        System.out.println("User object before save: " + user);

        user = userRepository.save(user);
        System.out.println("User saved with ID: " + user.getId());

        return userMapper.userToUserDto(user);
    }
}

