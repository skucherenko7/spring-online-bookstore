package mate.academy.spring.online.bookstore.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.mapper.UserMapper;
import mate.academy.spring.online.bookstore.model.Role;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.RoleRepository;
import mate.academy.spring.online.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
        public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("This email already exists");
        }
        User user = userMapper.requestDtoToUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByRole(Role.RoleName.USER);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }
}
