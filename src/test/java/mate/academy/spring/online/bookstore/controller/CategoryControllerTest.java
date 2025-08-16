package mate.academy.spring.online.bookstore.controller;

import static mate.academy.spring.online.bookstore.util.CategoryUtil.createCategoryRequestDto;
import static mate.academy.spring.online.bookstore.util.CategoryUtil.expectedNewCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.spring.online.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.online.bookstore.dto.category.CategoryDto;
import mate.academy.spring.online.bookstore.dto.category.CreateCategoryRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    private static final String INSERT_BOOKS_SCRIPT_PATH =
            "database/books/insert-books-with-categories-to-test-db.sql";
    private static final String INSERT_CATEGORIES_SCRIPT_PATH =
            "database/category/insert-categories-to-test-db.sql";
    private static final String REMOVE_ALL_SCRIPT_PATH =
            "database/delete-all-data-db.sql";
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_CATEGORIES_SCRIPT_PATH));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_BOOKS_SCRIPT_PATH));
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(REMOVE_ALL_SCRIPT_PATH));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @DisplayName("Create new valid category")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_ValidData_ShouldReturnCategoryDto() throws Exception {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        CategoryDto expectedDto = expectedNewCategory();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto resultDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);

        assertNotNull(resultDto);
        assertEquals(expectedDto.name(), resultDto.name());
        assertEquals(expectedDto.description(), resultDto.description());
    }

    @Test
    @DisplayName("Update category by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_ValidData_ShouldReturnUpdatedCategoryDto() throws Exception {
        Long categoryId = 2L;
        String updatedName = "UpdatedName";
        String updatedDescription = "UpdatedDescription";

        CreateCategoryRequestDto updateRequest = new CreateCategoryRequestDto(updatedName,
                updatedDescription);

        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto resultDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);

        assertNotNull(resultDto);
        assertEquals(categoryId, resultDto.id());
        assertEquals(updatedName, resultDto.name());
        assertEquals(updatedDescription, resultDto.description());
    }

    @Test
    @DisplayName("Update category by id without authorities")
    @WithMockUser(username = "user", roles = {"USER"})
    void updateCategory_WithoutPermission_ShouldReturnForbidden() throws Exception {
        Long categoryId = 1L;
        CreateCategoryRequestDto updateRequestDto = createCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update category with non-existent ID should return Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_NonExistentId_ShouldReturnNotFound() throws Exception {
        Long nonExistentId = 999L;
        CreateCategoryRequestDto updateRequestDto = createCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        mockMvc.perform(put("/categories/{id}", nonExistentId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get category by valid id")
    @WithMockUser(username = "user", roles = {"USER"})
    void getCategoryById_ValidId_ShouldReturnCategoryDto() throws Exception {
        // Ми явно вказуємо категорію з ID 1, яка є у твоєму SQL-скрипті
        CategoryDto expectedDto = new CategoryDto(
                1L,
                "Poetry",
                "Poetry books"
        );

        MvcResult result = mockMvc.perform(get("/categories/{id}", expectedDto.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        verifyCategoryDto(expectedDto, resultDto);
    }

    @Test
    @DisplayName("Get books by category ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void getBooksByCategoryId_ValidId_ShouldReturnListOfBooks() throws Exception {
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BookDtoWithoutCategoryIds[] books = objectMapper.readValue(jsonResponse,
                BookDtoWithoutCategoryIds[].class);

        assertNotNull(books);
        assertEquals(1, books.length, "Expected 1 books in category with ID 1");

        for (BookDtoWithoutCategoryIds book : books) {
            assertNotNull(book.title(), "The title of book cann’t be null");
            assertNotNull(book.author(), "The author of book cann’t be null");
        }
    }

    @Test
    @DisplayName("Get all categories with pagination")
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllCategories_ShouldReturnPagedCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = rootNode.get("content");

        assertNotNull(contentNode);
        assertTrue(contentNode.isArray());
        assertTrue(contentNode.size() > 0, "At least one category is expected in the list");

        JsonNode firstCategory = contentNode.get(0);
        assertNotNull(firstCategory.get("id"));
        assertNotNull(firstCategory.get("name"));
        assertNotNull(firstCategory.get("description"));
    }

    @Test
    @DisplayName("Delete category by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategory_ById_ShouldReturnNoContent() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete category without permission")
    @WithMockUser(username = "user",roles = {"USER"})
    void deleteCategory_WithoutPermission_ShouldReturnForbidden() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private void verifyCategoryDto(CategoryDto expected, CategoryDto actual) {
        assertNotNull(actual, "CategoryDto cann’t be null.");
        assertEquals(expected.id(), actual.id(), "Category ID does not match.");
        assertEquals(expected.name(), actual.name(), "The category name doesn’t match.");
        assertEquals(expected.description(),
                actual.description(), "Category description doesn’t match.");
    }
}
