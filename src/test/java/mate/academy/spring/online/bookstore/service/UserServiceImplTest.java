package mate.academy.spring.online.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.mapper.UserMapper;
import mate.academy.spring.online.bookstore.model.Role;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.role.RoleRepository;
import mate.academy.spring.online.bookstore.repository.user.UserRepository;
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartService;
import mate.academy.spring.online.bookstore.service.user.UserServiceImpl;
import mate.academy.spring.online.bookstore.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User userBeforeSave;
    private User userAfterSave;
    private UserRegistrationRequestDto requestDto;
    private UserResponseDto responseDto;
    private Role role;

    @BeforeEach
    void setUp() {
        userBeforeSave = UserUtil.getUserBeforeSaveIntoDb();
        userAfterSave = UserUtil.getUserAfterSaveIntoDb();
        requestDto = UserUtil.getUserRequestDto();
        responseDto = UserUtil.getUserResponseDto();
        role = UserUtil.getRoleUser();
    }

    @Test
    @DisplayName("Should return UserResponseDto when registration request is valid")
    void register_ShouldReturnUserResponseDto_WhenValidRequest() throws RegistrationException {
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.requestDtoToUser(requestDto)).thenReturn(userBeforeSave);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed_password");
        when(roleRepository.findByRole(Role.RoleName.ROLE_USER)).thenReturn(role);
        when(userRepository.save(userBeforeSave)).thenReturn(userAfterSave);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(responseDto);

        UserResponseDto actual = userService.register(requestDto);

        assertNotNull(actual);
        assertEquals(responseDto, actual);

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).requestDtoToUser(requestDto);
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(userBeforeSave);
        verify(shoppingCartService).createNewShoppingCart(userBeforeSave);
        verify(userMapper).userToUserDto(any(User.class));
    }

    @Test
    @DisplayName("Should throw RegistrationException when email already exists")
    void register_ShouldThrowRegistrationException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto)
        );

        assertEquals("This email already exists", exception.getMessage());

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userRepository, never()).save(any());
        verify(shoppingCartService, never()).createNewShoppingCart(any());
    }
}
