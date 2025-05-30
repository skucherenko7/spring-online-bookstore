package mate.academy.spring.online.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Add book to the shopping cart")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "/database/delete-all-data-db.sql",
            "/database/shoppingcarts/insert-test-data.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_shouldAddCartItem_integration() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Set<CartItemDto> cartItems = Set.of(
                new CartItemDto(1L, 1, "Seven Husbands of Evelyn Hugo", 2)
        );
        ShoppingCartDto expectedDto = new ShoppingCartDto(1L, 1L, cartItems);

        ShoppingCartDto actualDto = objectMapper.readValue(jsonResponse, ShoppingCartDto.class);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Get the shopping cart")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "/database/delete-all-data-db.sql",
            "/database/shoppingcarts/insert-cartitems-to-shoppingcart-test.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getShoppingCartByUser_shouldReturnCart() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.cartItems[0].id").value(1))
                .andExpect(jsonPath("$.cartItems[0].bookId").value(1))
                .andExpect(jsonPath("$.cartItems[0].bookTitle")
                        .value("Seven Husbands of Evelyn Hugo"))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2));
    }

    @Test
    @DisplayName("Update the cart item by id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "/database/delete-all-data-db.sql",
            "/database/shoppingcarts/insert-cartitems-to-shoppingcart-test.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_shouldUpdateCartItem() throws Exception {
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(5);

        mockMvc.perform(put("/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(5));
    }

    @Test
    @DisplayName("Delete the cart item by id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "/database/delete-all-data-db.sql",
            "/database/shoppingcarts/insert-cartitems-to-shoppingcart-test.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_shouldRemoveCartItem() throws Exception {
        mockMvc.perform(delete("/cart/items/1"))
                .andExpect(status().isNoContent());
    }
}
