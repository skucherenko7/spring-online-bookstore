package mate.academy.spring.online.bookstore.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemResponseDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.CartItem;
import mate.academy.spring.online.bookstore.model.ShoppingCart;

public class CartItemUtil {

    public static CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto(7L, 2);
    }

    public static CartItem createCartItem(CartItemRequestDto requestDto,
                                          ShoppingCart cart, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(cart);
        return cartItem;
    }

    public static CartItemResponseDto createCartItemResponseDto(CartItem cartItem) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getQuantity()
        );
    }

    public static void verifyCartItemResponseDto(CartItemResponseDto expected,
                                                 CartItemResponseDto actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.bookId(), actual.bookId());
        assertEquals(expected.quantity(), actual.quantity());
    }

    public static CartItemRequestDto getInvalidCartItemRequestDto() {
        return new CartItemRequestDto(null, -1);
    }
}

