package mate.academy.spring.online.bookstore.service.shoppingcart;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.mapper.CartItemMapper;
import mate.academy.spring.online.bookstore.mapper.ShoppingCartMapper;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.CartItem;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;
import mate.academy.spring.online.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.spring.online.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.spring.online.bookstore.repository.user.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;

    @Override
    public ShoppingCartDto addCartItem(CartItemRequestDto itemDto, Authentication authentication) {
        Long userId = findUser(authentication);

        ShoppingCart cart = shoppingCartRepository.findByUser_Id(userId);
        if (cart == null) {
            throw new EntityNotFoundException("Shopping Cart not found for user ID: " + userId);
        }

        Book book = bookRepository.findById(itemDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with ID: " + itemDto.bookId()));
        if (cart.getCartItems() == null) {
            cart.setCartItems(new HashSet<>());
        }

        CartItem cartItem = cartItemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);

        shoppingCartRepository.save(cart);

        ShoppingCart updatedCart = shoppingCartRepository.findByUser_Id(userId);

        return shoppingCartMapper.toDto(updatedCart);
    }

    @Override
    public ShoppingCartDto updateCartItemById(Long id,
                                              UpdateCartItemRequestDto requestDto,
                                              Authentication authentication) {
        Long userId = findUser(authentication);
        ShoppingCart cart = shoppingCartRepository.findByUser_Id(userId);

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(id, cart.getId())
                .map(item -> {
                    item.setQuantity(requestDto.getQuantity());
                    return item;
                })
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item by id " + id));
        cartItemRepository.save(cartItem);

        Hibernate.initialize(cart.getCartItems());

        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto getShoppingCartByUserId(Authentication authentication) {
        Long userId = findUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser_Id(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItemById(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Can`t find item by id " + id);
        }
        cartItemRepository.deleteById(id);
    }

    @Override
    public void createNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private Long findUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found by email: " + email))
                .getId();
    }

    public void addCartItemToCart(
            CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);
    }
}
