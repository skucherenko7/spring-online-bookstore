package mate.academy.spring.online.bookstore.dto.cartitem;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        int quantity
) {}
