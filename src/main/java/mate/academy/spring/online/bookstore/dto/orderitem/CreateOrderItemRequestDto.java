package mate.academy.spring.online.bookstore.dto.orderitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequestDto(
        @NotNull Long bookId,
        @Min(1) int quantity
) {}
