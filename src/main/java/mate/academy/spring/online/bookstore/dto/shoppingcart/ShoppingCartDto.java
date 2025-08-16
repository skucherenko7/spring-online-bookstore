package mate.academy.spring.online.bookstore.dto.shoppingcart;

import java.util.Set;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemDto> cartItems
) {
}
