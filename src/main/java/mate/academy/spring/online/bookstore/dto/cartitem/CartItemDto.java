package mate.academy.spring.online.bookstore.dto.cartitem;

public record CartItemDto(
        Long id,
        int bookId,
        String bookTitle,
        int quantity
) {
}
