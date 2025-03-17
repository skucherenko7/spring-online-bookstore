package mate.academy.spring.online.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import mate.academy.spring.online.bookstore.annotation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@FieldMatch(message = "Password and repeat password must be the same")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 20)
    private String password;

    @NotBlank
    @Length(min = 8, max = 20)
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Length(max = 255)
    private String shippingAddress;
}
