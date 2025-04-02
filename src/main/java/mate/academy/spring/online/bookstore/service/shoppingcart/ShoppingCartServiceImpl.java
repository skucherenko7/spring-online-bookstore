package mate.academy.spring.online.bookstore.service.shoppingcart;

import jakarta.transaction.Transactional;
import java.util.Optional;
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

    @Override
    public ShoppingCartDto addCartItem(
            CartItemRequestDto requestDto, Authentication authentication) {
        Long userId = findUser(authentication);

        Book book = bookRepository.findById(requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id "
                        + requestDto.bookId()));

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.bookId()))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + requestDto.quantity());
        } else {
            addCartItemToCart(requestDto, book, cart);
        }

        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto updateCartItemById(Long id,
                                              UpdateCartItemRequestDto requestDto,
                                              Authentication authentication) {
        Long userId = findUser(authentication);
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(id, cart.getId())
                .map(item -> {
                    item.setQuantity(requestDto.getQuantity());
                    return item;
                })
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item by id " + id));
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto getShoppingCartByUserId(Authentication authentication) {
        Long userId = findUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
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
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    private void addCartItemToCart(
            CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);
    }
}
