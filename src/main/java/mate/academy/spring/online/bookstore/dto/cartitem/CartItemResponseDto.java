package mate.academy.spring.online.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemResponseDto(
        @NotNull
        @Positive
        Long id,

        @NotNull
        @Positive
        Long bookId,

        @Positive
        int quantity
) {}
