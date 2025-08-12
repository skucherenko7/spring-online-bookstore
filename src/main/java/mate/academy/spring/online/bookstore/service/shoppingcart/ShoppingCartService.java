package mate.academy.spring.online.bookstore.service.shoppingcart;

import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(CartItemRequestDto requestDto, Authentication authentication);

    ShoppingCartDto updateCartItemById(Long id,
                                       UpdateCartItemRequestDto requestDto,
                                       Authentication authentication);

    ShoppingCartDto getShoppingCartByUserId(Authentication authentication);

    void deleteCartItemById(Long id);

    void createNewShoppingCart(User user);

    void addCartItemToCart(CartItemRequestDto itemDto, Book book, ShoppingCart cart);
}
