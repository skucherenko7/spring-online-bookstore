package mate.academy.spring.online.bookstore.repository;

import lombok.SneakyThrows;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    @SneakyThrows
    void cleanDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/delete-all-users-db.sql"));
        }
    }

    private User createAndSaveTestUser() {
        User user = new User();
        user.setEmail("user111@example.com");
        user.setPassword("$2a$10$WlHqEbjYKo0FKQUYC1ppUOGaDj7dRW6USZ7gIzt9UXkajr4X2wXC2");
        user.setFirstName("User111");
        user.setLastName("User111");
        user.setShippingAddress("145 Main St, City, Country");
        userRepository.save(user);
        userRepository.flush();
        return user;
    }

    @Test
    @DisplayName("Return true when user exists")
    void existsByEmail_ExistUser_ShouldReturnTrue() {
        createAndSaveTestUser();

        boolean result = userRepository.existsByEmail("user111@example.com");

        assertTrue(result);
    }

    @Test
    @DisplayName("Return false when user doesn't exist")
    void existsByEmail_NoExistUser_ShouldReturnFalse() {
        boolean result = userRepository.existsByEmail("noExist.userEmail@gmail.com");

        assertFalse(result);
    }

    @Test
    @DisplayName("Return optional user when user exists")
    void findByEmail_ExistUser_ShouldReturnOptionalUser() {
        User expected = createAndSaveTestUser();

        Optional<User> optionalUser = userRepository.findByEmail("user111@example.com");

        assertTrue(optionalUser.isPresent());
        assertThat(optionalUser.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Return empty when user doesn't exist")
    void findByEmail_NoExistUser_ShouldReturnEmpty() {
        Optional<User> optionalUser = userRepository.findByEmail("noExist.userEmail@gmail.com");

        assertTrue(optionalUser.isEmpty());
    }
}
