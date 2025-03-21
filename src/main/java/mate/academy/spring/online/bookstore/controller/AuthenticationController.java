package mate.academy.spring.online.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.user.UserLoginRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserLoginResponseDto;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.online.bookstore.dto.user.UserResponseDto;
import mate.academy.spring.online.bookstore.exception.RegistrationException;
import mate.academy.spring.online.bookstore.sequrity.AuthenticationService;
import mate.academy.spring.online.bookstore.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication and registration",
        description = "Endpoints for user authentication and registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "user registration",
            description = "user registration by email, password and repeatPassword")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Endpoint for login existing user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
