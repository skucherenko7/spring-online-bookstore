package mate.academy.spring.online.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.spring.online.bookstore.dto.user.UserLoginRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserLoginResponseDto;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.CustomGlobalExceptionHandler;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.security.AuthenticationService;
import mate.academy.spring.online.bookstore.service.user.UserService;
import mate.academy.spring.online.bookstore.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto userResponseDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        userRegistrationRequestDto = UserUtil.getUserRequestDto();
        userResponseDto = UserUtil.getUserResponseDto();
    }

    @Test
    void registerUser_ShouldReturnUserResponseDto_WhenValidRequest() throws Exception {
        ArgumentCaptor<UserRegistrationRequestDto> captor = ArgumentCaptor
                .forClass(UserRegistrationRequestDto.class);
        when(userService.register(captor.capture())).thenReturn(userResponseDto);

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRegistrationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.getLastName()))
                .andExpect(jsonPath("$.shippingAddress")
                        .value(userResponseDto.getShippingAddress()));

        assertEquals(userRegistrationRequestDto.getEmail(), captor.getValue().getEmail());
        verify(userService).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    void registerUser_ShouldThrowRegistrationException_WhenEmailAlreadyExists() throws Exception {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setEmail("testUser@ukr.net");
        userRegistrationRequestDto.setPassword("12345678");
        userRegistrationRequestDto.setRepeatPassword("12345678");
        userRegistrationRequestDto.setFirstName("TestUserName1");
        userRegistrationRequestDto.setLastName("TestLastName1");
        userRegistrationRequestDto.setShippingAddress("TesShippingAddress 1");

        when(userService.register(any(UserRegistrationRequestDto.class)))
                .thenThrow(new RegistrationException("This email already exists"));

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This email already exists"));
    }

    @Test
    void loginUser_ShouldReturnLoginResponse_WhenValidCredentials() throws Exception {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(
                "testUser@ukr.net",
                "12345678"
        );

        UserLoginResponseDto loginResponseDto = new UserLoginResponseDto("fake-jwt-token");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(loginResponseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void loginUser_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(
                "testUser@ukr.net",
                "wrongPassword"
        );

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
