package mate.academy.spring.online.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {
    @NotBlank(message = "Shipping Address is required!")
    private String shippingAddress;
}
