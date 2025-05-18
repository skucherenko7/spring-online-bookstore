package mate.academy.spring.online.bookstore.repository;

import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.repository.shoppingcart.ShoppingCartRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {

    private static final String DELETE_ALL_SCRIPT = "database/delete-all-data-db.sql";
    private static final String INSERT_BASE_SCRIPT = "database/shoppingcarts/insert-base-data.sql";

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("database/delete-all-data-db.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("database/shoppingcarts/insert-base-data.sql"));
        }
    }

    @AfterAll
    static void teardown(@Autowired DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(DELETE_ALL_SCRIPT));
        }
    }

    @Test
    @DisplayName("Should find shopping cart with user and cart items")
    void findByUserId_ShouldReturnCartWithItems() {
        Long userId = 1L;

        ShoppingCart cart = shoppingCartRepository.findByUser_Id(userId);
        assertNotNull(cart, "ShoppingCart not found");
    }
}
