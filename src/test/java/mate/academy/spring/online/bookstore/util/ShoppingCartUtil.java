package mate.academy.spring.online.bookstore.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.CartItem;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;

public class ShoppingCartUtil {
    public static ShoppingCartDto createShoppingCartRequestDto() {
        Set<CartItemDto> cartItems = new HashSet<>();

        cartItems.add(new CartItemDto(1L, 1, "Seven Husbands of Evelyn Hugo", 2));
        cartItems.add(new CartItemDto(2L, 2, "Lisova pisnya", 2));

        return new ShoppingCartDto(50L, 50L, cartItems);
    }

    public static ShoppingCart createShoppingCart(ShoppingCartDto dto, User user,
                                                  BookRepository bookRepository) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        Set<CartItem> cartItems = new HashSet<>();
        for (CartItemDto itemDto : dto.cartItems()) {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(itemDto.quantity());

            Book book = bookRepository.findById(Long.valueOf(itemDto.bookId()))
                    .orElseThrow(() -> new RuntimeException("Book not found with "
                            + "ID: " + itemDto.bookId()));
            cartItem.setBook(book);

            shoppingCart.addItemToCart(cartItem);
        }

        return shoppingCart;
    }

    public static ShoppingCartResponseDto createShoppingCartResponseDto(ShoppingCart shoppingCart) {
        return new ShoppingCartResponseDto(shoppingCart.getId(), shoppingCart.isDeleted());

    }

    public static void verifyShoppingCart(ShoppingCart expected, ShoppingCart actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());

        for (CartItem expectedItem : expected.getCartItems()) {
            boolean matchFound = actual.getCartItems().stream().anyMatch(actualItem ->
                    actualItem.getBook().getId().equals(expectedItem.getBook().getId())
                            && actualItem.getQuantity() == expectedItem.getQuantity()
            );
            assertTrue(matchFound, "Expected item not found: " + expectedItem.getBook().getId());
        }
    }

    public static ShoppingCartDto getInvalidShoppingCartDto() {
        return new ShoppingCartDto(null, null, null);
    }
}
