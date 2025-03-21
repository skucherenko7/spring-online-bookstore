package mate.academy.spring.online.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Size(max = 255)
        @Email
        String email,

        @NotBlank
        @Size(max = 255)
        String password
) {
}
