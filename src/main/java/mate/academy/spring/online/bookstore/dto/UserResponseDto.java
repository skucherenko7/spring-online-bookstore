package mate.academy.spring.online.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String shippingAddress;
}
