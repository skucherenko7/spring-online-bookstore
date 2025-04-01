package mate.academy.spring.online.bookstore.service.user;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.mapper.UserMapper;
import mate.academy.spring.online.bookstore.model.Role;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.role.RoleRepository;
import mate.academy.spring.online.bookstore.repository.user.UserRepository;
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("This email already exists");
        }
        User user = userMapper.requestDtoToUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByRole(Role.RoleName.ROLE_USER);
        user.setRoles(new HashSet<>(List.of(role)));
        userRepository.save(user);
        shoppingCartService.createNewShoppingCart(user);
        return userMapper.userToUserDto(user);
    }
}
