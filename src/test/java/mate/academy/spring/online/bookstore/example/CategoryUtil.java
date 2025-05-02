package mate.academy.spring.online.bookstore.example;

import mate.academy.spring.online.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.online.bookstore.dto.category.CategoryDto;
import mate.academy.spring.online.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.spring.online.bookstore.model.Category;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class CategoryUtil {
    public static Category createCategory() {
        Long id = 2L;
        String name = "Poetry";
        String description = "Poetry books";
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setIsDeleted(false);
        return category;
    }

    public static CategoryDto createCategoryResponseDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    public static CreateCategoryRequestDto updateCategoryRequestDto() {
        String updateName = "Updated Poetry";
        String updateDescription = "Updated description";
        return new CreateCategoryRequestDto(updateName, updateDescription);
    }

    public static List<BookDtoWithoutCategoryIds> createBooksList() {
        BookDtoWithoutCategoryIds book1 = new BookDtoWithoutCategoryIds(
                1L,
                "Seven Husbands of Evelyn Hug",
                "Taylor Jenkins Reid",
                "978-1234567989",
                BigDecimal.valueOf(520),
                "Updated description",
                "https://example.com/updated-cover-image.jpg"
        );

        BookDtoWithoutCategoryIds book2 = new BookDtoWithoutCategoryIds(
                2L,
                "Lisova pisnya",
                "Ukrainka",
                "978-1234567979",
                BigDecimal.valueOf(670),
                "Updated description",
                "https://example.com/updated-cover-image.jpg"
        );

        return Arrays.asList(book1, book2);
    }


    public static CategoryDto updateCategory(Long id, String name, String description) {
        return new CategoryDto(id, name, description);
    }

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        String name = "createName";
        String description = "createDescription";
        return new CreateCategoryRequestDto(name, description);
    }

    public static CategoryDto expectedCategoryResponseDto() {
        String testName = "Poetry";
        String testDescription = "Poetry books";
        Long testId = 2L;
        return new CategoryDto(testId, testName, testDescription);
    }

    public static CategoryDto expectedNewCategory() {
        String name = "createName";
        String description = "createDescription";
        return new CategoryDto(null, name, description);
    }}
