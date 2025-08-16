package mate.academy.spring.online.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @Positive
        @NotNull
        Long bookId,

        @Positive
        int quantity
) {
}
