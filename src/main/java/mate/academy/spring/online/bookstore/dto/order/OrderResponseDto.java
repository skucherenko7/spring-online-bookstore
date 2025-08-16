package mate.academy.spring.online.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;

public record OrderResponseDto(Long id,
                               Long userId,
                               Set<OrderItemResponseDto> orderItems,
                               LocalDateTime orderDate,
                               BigDecimal total,
                               String status) {
}
