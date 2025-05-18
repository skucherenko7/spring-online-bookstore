package mate.academy.spring.online.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.exception.BookNotFoundException;
import mate.academy.spring.online.bookstore.exception.CustomGlobalExceptionHandler;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import java.util.Set;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private BookRepository bookRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(shoppingCartController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();
    }

    private ShoppingCartDto stubShoppingCartDto() {
        CartItemDto cartItemDto = new CartItemDto(1L, 12, "Son", 2);
        Set<CartItemDto> cartItems = Set.of(cartItemDto);
        return new ShoppingCartDto(1L, 1L, cartItems);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getShoppingCartByUser_shouldReturnCart() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void save_shouldAddCartItem() throws Exception {
        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("Seven Husbands of Evelyn Hugo");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        Mockito.when(shoppingCartService.addCartItem(any(), any()))
                .thenReturn(stubShoppingCartDto());

        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 2);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void update_shouldUpdateCartItem() throws Exception {
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(5);

        when(shoppingCartService.updateCartItemById(eq(1L), any(), any()))
                .thenReturn(stubShoppingCartDto());

        mockMvc.perform(put("/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void delete_shouldThrowException() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(shoppingCartService).deleteCartItemById(1L);

        mockMvc.perform(delete("/cart/items/{id}", 1L))
                .andExpect(status().isNotFound());  // Очікуємо статус 404, якщо книга не знайдена
    }
}
