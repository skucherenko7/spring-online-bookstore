package mate.academy.spring.online.bookstore.repository;

import mate.academy.spring.online.bookstore.model.CartItem;
import mate.academy.spring.online.bookstore.repository.cartitem.CartItemRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {

    private static final String INSERT_BASE_DATA_SCRIPT =
            "database/cartitems/insert-books-and-carts-for-cartitems.sql";
    private static final String INSERT_CART_ITEMS_SCRIPT =
            "database/cartitems/insert-cart-items-to-test-db.sql";
    private static final String DELETE_ALL_SCRIPT =
            "database/delete-all-data-db.sql";

    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(INSERT_BASE_DATA_SCRIPT));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(INSERT_CART_ITEMS_SCRIPT));
        }
    }

    @AfterAll
    static void teardown(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(DELETE_ALL_SCRIPT));
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean up test DB", e);
        }
    }

    @Test
    @DisplayName("Should find CartItem by cart ID and book ID")
    void findByShoppingCartIdAndBookId_ShouldReturnCartItem() {
        Long cartId = 1L;
        Long bookId = 3L;

        CartItem cartItem = cartItemRepository.findByShoppingCartIdAndBookId(cartId, bookId);

        assertNotNull(cartItem);
        assertEquals(2, cartItem.getQuantity());
        assertEquals(bookId, cartItem.getBook().getId());
    }

    @Test
    @DisplayName("Should return empty optional if CartItem does not exist by ID and Cart ID")
    void findByIdAndShoppingCartId_ShouldReturnEmptyIfNotExists() {
        Optional<CartItem> result = cartItemRepository.findByIdAndShoppingCartId(999L, 1L);

        assertTrue(result.isEmpty());
    }
}
