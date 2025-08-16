package mate.academy.spring.online.bookstore.dto.orderitem;

public record OrderItemResponseDto(Long id, Long bookId, int quantity) {
}
