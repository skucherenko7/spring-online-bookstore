package mate.academy.spring.online.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import mate.academy.spring.online.bookstore.model.OrderItem;
import mate.academy.spring.online.bookstore.repository.order.OrderItemRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest {

    private static final String INSERT_BASE_DATA_SCRIPT =
            "database/orderitems/insert-users-books-orders.sql";
    private static final String INSERT_ORDER_ITEMS_SCRIPT =
            "database/orderitems/insert-order-items.sql";
    private static final String DELETE_ALL_SCRIPT =
            "database/delete-all-data-db.sql";

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(INSERT_BASE_DATA_SCRIPT));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(INSERT_ORDER_ITEMS_SCRIPT));
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
    @DisplayName("Should find OrderItem by ID, Order ID and User ID")
    void findByIdAndOrderIdAndUserId_ShouldReturnOrderItem() {
        Long orderItemId = 1L;
        Long orderId = 1L;
        Long userId = 1L;

        Optional<OrderItem> result = orderItemRepository.findByIdAndOrder_IdAndOrder_User_Id(
                orderItemId, orderId, userId
        );

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getQuantity());
        assertEquals(1L, result.get().getBook().getId());
    }

    @Test
    @DisplayName("Should return empty if user ID is incorrect")
    void findByIdAndOrderIdAndUserId_ShouldReturnEmptyIfWrongUserId() {
        Optional<OrderItem> result = orderItemRepository.findByIdAndOrder_IdAndOrder_User_Id(
                1L, 1L, 999L
        );

        assertTrue(result.isEmpty());
    }
}
