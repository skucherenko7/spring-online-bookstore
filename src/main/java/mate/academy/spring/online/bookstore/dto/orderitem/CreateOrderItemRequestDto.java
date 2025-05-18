package mate.academy.spring.online.bookstore.dto.orderitem;

public record CreateOrderItemRequestDto(Long bookId, int quantity) {
}
