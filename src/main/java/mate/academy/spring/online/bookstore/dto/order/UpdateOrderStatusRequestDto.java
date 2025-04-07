package mate.academy.spring.online.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import mate.academy.spring.online.bookstore.model.Order.Status;

public record UpdateOrderStatusRequestDto(
        @NotNull(message = "Status cannot be null")
        Status status) { }
