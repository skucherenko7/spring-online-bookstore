package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.exception.BookNotFoundException;
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
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private Authentication authentication;

    private User user;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());
    }

    @Test
    void addCartItem_shouldAddNewItem() {
        Long bookId = 10L;
        Book book = new Book();
        book.setId(bookId);

        CartItemRequestDto requestDto = new CartItemRequestDto(bookId, 2);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(cart);

        CartItem newCartItem = new CartItem();
        newCartItem.setBook(book);
        newCartItem.setQuantity(2);

        when(cartItemMapper.toModel(requestDto)).thenReturn(newCartItem);

        CartItemDto cartItemDto = new CartItemDto(1L, bookId.intValue(), "Book title", 2);
        Set<CartItemDto> cartItemDtos = Set.of(cartItemDto);

        ShoppingCartDto expectedDto = new ShoppingCartDto(1L, 1L, cartItemDtos);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);

        ShoppingCartDto result = shoppingCartService.addCartItem(requestDto, authentication);

        verify(bookRepository).findById(bookId);
        verify(cartItemMapper).toModel(requestDto);
        verify(shoppingCartRepository).save(cart);
        assertEquals(expectedDto, result);
    }

    @Test
    void updateCartItemById_shouldUpdateQuantity() {
        Long cartItemId = 5L;
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(4);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(1);

        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(cart);
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.of(cartItem));

        ShoppingCartDto expectedDto = new ShoppingCartDto(1L, user.getId(), Set.of());
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);

        ShoppingCartDto result = shoppingCartService.updateCartItemById(cartItemId, updateDto, authentication);

        verify(cartItemRepository).save(cartItem);
        assertEquals(4, cartItem.getQuantity());
        assertEquals(expectedDto, result);
    }

    @Test
    void updateCartItemById_shouldThrowIfItemNotFound() {
        Long cartItemId = 999L;
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(3);

        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(cart);
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemById(cartItemId, updateDto, authentication)
        );

        assertTrue(exception.getMessage().contains("Can't find cart item by id"));
    }

    @Test
    void getShoppingCartByUserId_shouldReturnCartDto() {
        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(cart);

        ShoppingCartDto expectedDto = new ShoppingCartDto(cart.getId(), user.getId(), Set.of());
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);

        ShoppingCartDto result = shoppingCartService.getShoppingCartByUserId(authentication);

        assertEquals(expectedDto, result);
    }

    @Test
    void deleteCartItemById_shouldDeleteIfExists() {
        Long cartItemId = 10L;
        when(cartItemRepository.existsById(cartItemId)).thenReturn(true);

        shoppingCartService.deleteCartItemById(cartItemId);

        verify(cartItemRepository).deleteById(cartItemId);
    }

    @Test
    void deleteCartItemById_shouldThrowIfNotExists() {
        Long cartItemId = 20L;
        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.deleteCartItemById(cartItemId)
        );

        assertTrue(exception.getMessage().contains("Can`t find item by id"));
    }

    @Test
    void createNewShoppingCart_shouldSaveNewCart() {
        User newUser = new User();
        newUser.setId(2L);

        shoppingCartService.createNewShoppingCart(newUser);

        verify(shoppingCartRepository).save(argThat(cart -> cart.getUser().equals(newUser)));
    }

    @Test
    void addCartItem_shouldFindUser() {
        Long bookId = 10L;
        Book book = new Book();
        book.setId(bookId);

        CartItemRequestDto requestDto = new CartItemRequestDto(bookId, 2);

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        cartItem.setBook(book);

        when(authentication.getPrincipal()).thenReturn(user);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(cart);
        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem);

        shoppingCartService.addCartItem(requestDto, authentication);

        assertNotNull(cart.getCartItems(), "Cart items should not be null");
        assertTrue(cart.getCartItems().size() > 0, "There should be at least one cart item");
        assertEquals(bookId, cart.getCartItems().iterator().next().getBook().getId());
    }


    private void addCartItemToCart(CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toModel(itemDto);
        if (cartItem == null) {
            throw new IllegalArgumentException("CartItem is null");
        }
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);
    }

    @Test
    void addCartItem_shouldThrowIfBookNotFound() {
        Long bookId = 100L;
        CartItemRequestDto requestDto = new CartItemRequestDto(bookId, 1);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () ->
                shoppingCartService.addCartItem(requestDto, authentication)
        );
    }

    @Test
    void addCartItemToCart_shouldAddItemCorrectly() {
        Long bookId = 15L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(5L);
        shoppingCart.setCartItems(new HashSet<>());

        CartItemRequestDto requestDto = new CartItemRequestDto(bookId, 3);

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(3);

        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem);

        shoppingCartService.addCartItemToCart(requestDto, book, shoppingCart);

        assertEquals(1, shoppingCart.getCartItems().size());
        CartItem addedItem = shoppingCart.getCartItems().iterator().next();
        assertEquals(book, addedItem.getBook());
        assertEquals(3, addedItem.getQuantity());
    }
}
