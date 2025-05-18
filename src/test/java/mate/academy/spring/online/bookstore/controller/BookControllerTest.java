package mate.academy.spring.online.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.spring.online.bookstore.dto.book.BookDto;
import mate.academy.spring.online.bookstore.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static mate.academy.spring.online.bookstore.example.BookUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {private static final String INSERT_BOOKS_SCRIPT_PATH =
        "database/books/insert-books-with-categories-to-test-db.sql";
    private static final String INSERT_CATEGORIES_SCRIPT_PATH =
            "database/category/insert-categories-to-test-db.sql";
    private static final String REMOVE_ALL_SCRIPT_PATH =
            "database/delete-all-data-db.sql";
    private static MockMvc mockMvc;

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

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(REMOVE_ALL_SCRIPT_PATH));
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }
        @Test
        @DisplayName("Create book with valid data")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void createBook_ValidData_ShouldReturnCreatedBookDto() throws Exception {
            Long testId = 3L;

            BookDto expectedDto = createExpectedBookDto(testId);
            CreateBookRequestDto requestDto = creatBookRequestDto();

            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            MvcResult result = mockMvc.perform(post("/books")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            BookDto resultDto = objectMapper.readValue(
                    result.getResponse().getContentAsString(), BookDto.class);

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

        CreateBookRequestDto requestDto = creatBookRequestDto();
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
    void deleteBook_InvalidId_ShouldReturnNoContent() throws Exception {
        Long invalidId = 999L;

        mockMvc.perform(delete("/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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

        verifyBookDtoEquality (expectedDto, resultDto);
    }

    @Test
    @DisplayName("Search books by parameters")
    @WithMockUser(username = "user", roles = {"USER"})
    void searchBooks_ShouldReturnFilteredBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("title", "Seven Husbands of Evelyn Hugo") // або інші значення параметрів
                        .param("author", "Taylor Jenkins Reid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertTrue(response.contains("Seven Husbands of Evelyn Hugo")); // перевірка наявності
    }

    @Test
    @DisplayName("Get all books paginated")
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllBooks_ShouldReturnPagedBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertTrue(response.contains("content")); // перевірка, що повертається сторінка
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
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getCategoriesIds(), actual.getCategoriesIds());
    }

}
