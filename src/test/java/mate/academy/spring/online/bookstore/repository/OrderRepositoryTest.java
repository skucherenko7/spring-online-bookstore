package mate.academy.spring.online.bookstore.repository;

import mate.academy.spring.online.bookstore.model.Order;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.order.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    private static final String INSERT_BASE_DATA_SCRIPT =
            "database/orderitems/insert-users-books-orders.sql";
    private static final String DELETE_ALL_SCRIPT =
            "database/delete-all-data-db.sql";

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(INSERT_BASE_DATA_SCRIPT));
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
    @DisplayName("Should find orders by user")
    void findByUser_ShouldReturnOrders() {
        User user = new User();
        user.setId(1L);

        Page<Order> result = orderRepository.findByUser(user, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getUser().getId());
    }

    @Test
    @DisplayName("Should find order by ID and User ID")
    void findByIdAndUserId_ShouldReturnOrder() {
        Optional<Order> result = orderRepository.findByIdAndUserId(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getUser().getId());
    }

    @Test
    @DisplayName("Should return empty if order does not belong to user")
    void findByIdAndUserId_ShouldReturnEmptyForWrongUser() {
        Optional<Order> result = orderRepository.findByIdAndUserId(1L, 999L);

        assertTrue(result.isEmpty());
    }
}
