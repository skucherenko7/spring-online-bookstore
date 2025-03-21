package mate.academy.spring.online.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(max = 255)
        @Email
        String email,

        @NotEmpty
        @Size(max = 255)
        String password
) {
}
