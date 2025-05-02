package mate.academy.spring.online.bookstore.controller;

import lombok.SneakyThrows;
import mate.academy.spring.online.bookstore.dto.book.BookDto;
import mate.academy.spring.online.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.service.book.BookService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import static mate.academy.spring.online.bookstore.example.BookUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)

public class BookControllerTest {
    private static final String INSERT_BOOKS_SCRIPT_PATH =
            "database/books/insert-books-with-categories-to-test-db.sql";
    private static final String INSERT_CATEGORIES_SCRIPT_PATH =
            "database/category/insert-categories-to-test-db.sql";
    private static final String REMOVE_ALL_SCRIPT_PATH =
            "database/delete-all-data-db.sql";
    private static MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        teardown(dataSource);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_CATEGORIES_SCRIPT_PATH));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_BOOKS_SCRIPT_PATH));
        }
    }

    @Test
    @DisplayName("Create book with valid data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_ValidData_ShouldReturnCreatedBookDto() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Kobzar")
                .setAuthor("Shevchenko")
                .setIsbn("978-1234561999")
                .setPrice(new BigDecimal("1200.00"))
                .setDescription("Updated description")
                .setCoverImage("https://example.com/updated-cover-image.jpg")
                .setCategories(Set.of(2L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto resultDto = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        Long createdBookId = resultDto.getId();

        BookDto expectedDto = createExpectedBookDto(createdBookId);

        verifyBookDtoEquality(expectedDto, resultDto);
    }

    @Test
    @DisplayName("Create book with invalid data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_NotValidData_ShouldReturnException() throws Exception {
        CreateBookRequestDto requestDto = getInvalidCreateBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertTrue(responseContent.contains("price must be greater than 0"),
                "Expected validation message for price");
    }

    @Test
    @DisplayName("Update book with valid data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBook_ValidData_ShouldReturnUpdatedBookDto() throws Exception {
        Long testId = 1L;

        BookDto expectedDto = createExpectedBookDto(
                testId);
        CreateBookRequestDto requestDto = creatBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", testId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        verifyBookDtoEquality(expectedDto, resultDto);
    }

    @Test
    @DisplayName("Update book with invalid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBook_InvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book")
                .setAuthor("Author")
                .setIsbn("978-1234567890")
                .setPrice(new BigDecimal("10.00"))
                .setCategories(Set.of(1L));

        when(bookService.updateBook(eq(invalidId), eq(requestDto)))
                .thenThrow(new EntityNotFoundException("Book not found by id " + invalidId));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", invalidId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Book not found by id " + invalidId));
    }

    @Test
    @DisplayName("Delete book by valid id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_ById_ShouldReturnNoContent() throws Exception {
        Long testId = 1L;

        mockMvc.perform(delete("/books/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Delete book with invalid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_InvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        mockMvc.perform(delete("/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get book by valid id")
    @WithMockUser(username = "user", roles = {"USER"})
    void getBook_ByValidId_ShouldReturnBookDto() throws Exception {
        Long testId = 1L;
        BookDto expectedDto = getBookDto(testId);

        MvcResult result = mockMvc.perform(get("/books/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        verifyBookDtoEquality(expectedDto, resultDto);
    }

    @Test
    @DisplayName("Get book with invalid ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void getBook_ByInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        mockMvc.perform(get("/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private void verifyBookDtoEquality(BookDto expected, BookDto actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCoverImage(), actual.getCoverImage());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()), "Prices should be equal ignoring scale");

        assertEquals(
                new HashSet<>(expected.getCategoriesIds()),
                new HashSet<>(actual.getCategoriesIds()),
                "Category IDs should match regardless of order"
        );
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(BookControllerTest.REMOVE_ALL_SCRIPT_PATH));
        }
    }
}
