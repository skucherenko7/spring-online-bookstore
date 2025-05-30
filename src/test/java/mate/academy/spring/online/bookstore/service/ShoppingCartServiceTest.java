package mate.academy.spring.online.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.CartItem;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;
import mate.academy.spring.online.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.spring.online.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.spring.online.bookstore.repository.user.UserRepository;
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Transactional
@Sql(scripts = "/database/delete-all-data-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ShoppingCartServiceTest {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private User user;
    private Book book;
    private ShoppingCart cart;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user111@util.com");
        user.setPassword("Password111");
        user.setFirstName("User111");
        user.setLastName("User111");
        user.setShippingAddress("145 Main St, City, Country");
        user = userRepository.save(user);

        book = new Book();
        book.setTitle("Seven Husbands of Evelyn Hugo");
        book.setAuthor("Taylor Jenkins Reid");
        book.setIsbn("978-1234567989");
        book.setPrice(new BigDecimal("520.00"));
        book = bookRepository.save(book);

        shoppingCartService.createNewShoppingCart(user);
        cart = shoppingCartRepository.findByUser_Id(user.getId());

        authentication = new TestingAuthenticationToken(user, null);
    }

    @Test
    @DisplayName("Adding  a new book to the shoppingcart")
    void addCartItem_shouldAddNewItem() {
        CartItemRequestDto requestDto = new CartItemRequestDto(book.getId(), 2);
        ShoppingCartDto result = shoppingCartService.addCartItem(requestDto, authentication);

        assertNotNull(result);
        assertEquals(1, result.cartItems().size());
        var item = result.cartItems().iterator().next();
        assertEquals(book.getId(), item.bookId());
        assertEquals(2, item.quantity());
    }

    @Test
    @DisplayName("Updating a quantity to the shoppingcart")
    void updateCartItemById_shouldUpdateQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(1);

        cart.addItemToCart(cartItem);
        shoppingCartRepository.saveAndFlush(cart);

        cartItem = cart.getCartItems().iterator().next();

        CartItem savedCartItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        assertEquals(1, savedCartItem.getQuantity());

        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(4);

        ShoppingCartDto result = shoppingCartService.updateCartItemById(cartItem.getId(),
                updateDto, authentication);

        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        assertEquals(4, updatedCartItem.getQuantity());

        assertNotNull(result.cartItems(), "Cart items should not be null");
        assertEquals(1, result.cartItems().size(), "Cart items size should be 1");
        assertEquals(4, result.cartItems().iterator().next()
                .quantity(), "Quantity in DTO should be updated");
    }

    @Test
    @DisplayName("It’s exception if book was not found by id, when updating was been")
    void updateCartItemById_shouldThrowIfItemNotFound() {
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(3);

        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.updateCartItemById(999L, updateDto, authentication));
    }

    @Test
    @DisplayName("Returnig the shoppingcard of user")
    void getShoppingCartByUserId_shouldReturnCartDto() {
        ShoppingCartDto result = shoppingCartService.getShoppingCartByUserId(authentication);
        assertEquals(user.getId(), result.userId());
    }

    @Test
    @DisplayName("Deleting a shoppingcart if it’s existing")
    void deleteCartItemById_shouldDeleteIfExists() {
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cartItem.setShoppingCart(cart);
        cartItem = cartItemRepository.save(cartItem);

        shoppingCartService.deleteCartItemById(cartItem.getId());

        assertFalse(cartItemRepository.existsById(cartItem.getId()));
    }

    @Test
    @DisplayName("Deleting a shoppingcart if it’s existing")
    void deleteCartItemById_shouldThrowIfNotExists() {
        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.deleteCartItemById(9999L));
    }

    @Test
    @DisplayName("Creating a new shoppingcart")
    void createNewShoppingCart_shouldCreateForUser() {
        User newUser = new User();
        newUser.setEmail("newuser111@ex.com");
        newUser.setPassword("Password111");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setShippingAddress("445 Main St, City, Country");
        newUser = userRepository.save(newUser);

        shoppingCartService.createNewShoppingCart(newUser);

        ShoppingCart found = shoppingCartRepository.findByUser_Id(newUser.getId());
        assertNotNull(found);
        assertEquals(newUser.getId(), found.getUser().getId());
    }

    @Test
    @DisplayName("Exception if a book was’t found")
    void addCartItem_shouldThrowIfBookNotFound() {
        CartItemRequestDto requestDto = new CartItemRequestDto(999L, 1);
        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.addCartItem(requestDto, authentication));
    }

    @Test
    @DisplayName("Adding a book сorrectly")
    void addCartItemToCart_shouldAddItemCorrectly() {
        CartItemRequestDto requestDto = new CartItemRequestDto(book.getId(), 3);
        shoppingCartService.addCartItemToCart(requestDto, book, cart);

        assertEquals(1, cart.getCartItems().size());
        CartItem added = cart.getCartItems().iterator().next();
        assertEquals(book.getId(), added.getBook().getId());
        assertEquals(3, added.getQuantity());
    }
}
