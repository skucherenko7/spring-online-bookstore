package mate.academy.spring.online.bookstore.dto.order;

public record UpdateOrderStatusRequestDto(Long orderId,
                                          String status) {
}
