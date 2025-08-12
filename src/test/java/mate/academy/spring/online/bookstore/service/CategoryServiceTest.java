package mate.academy.spring.online.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.spring.online.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.online.bookstore.dto.category.CategoryDto;
import mate.academy.spring.online.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.mapper.CategoryMapper;
import mate.academy.spring.online.bookstore.model.Category;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;
import mate.academy.spring.online.bookstore.repository.category.CategoryRepository;
import mate.academy.spring.online.bookstore.service.category.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Save category with valid request should return CategoryDto")
    void save_ValidCreateCategoryDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("Fiction",
                "Fiction books");

        Category category = new Category()
                .setName(requestDto.name())
                .setDescription(requestDto.description());

        CategoryDto categoryDto = new CategoryDto(1L, category.getName(),
                category.getDescription());

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(requestDto);

        assertThat(result).isEqualTo(categoryDto);

        verify(categoryMapper).toModel(requestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by valid ID should return correct category")
    void getById_WithValidCategoryById_ShouldReturnValidCategory() {
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fiction");
        category.setDescription("Fiction books");

        CategoryDto categoryDto = new CategoryDto(1L, category.getName(),
                category.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findCategoryById(categoryId);

        assertThat(result).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Get category by non-existing ID should throw exception")
    void getById_WithNonExistingCategoryId_ShouldThrowEntityNotFoundException() {
        Long categoryId = 100L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.findCategoryById(categoryId));
    }

    @Test
    @DisplayName("Delete category by valid ID should delete category")
    void deleteById_ValidDeleteCategory_DeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteById(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Find all categories with valid pageable should return category list")
    void findAll_ValidPageable_ReturnsAllCategory() {
        Category category = new Category()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Fiction book");

        CategoryDto categoryDto = new CategoryDto(category.getId(),
                category.getName(), category.getDescription());

        Pageable pageable = PageRequest.of(0, 10);

        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertThat(categoryDtos.getContent()).containsExactly(categoryDto);
    }

    @Test
    @DisplayName("Update category with valid data should update category")
    void update_ValidUpdateCategory_UpdateCategory() {
        Long categoryId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("Updated Fiction",
                "Updated description");

        Category category = new Category();
        category.setId(categoryId);
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());

        CategoryDto categoryDto = new CategoryDto(categoryId, category.getName(),
                category.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        doNothing().when(categoryMapper).updateCategoryFromDto(category, requestDto);
        doReturn(categoryDto).when(categoryMapper).toDto(any(Category.class));

        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto updatedCategory = categoryService.update(categoryId, requestDto);

        assertThat(updatedCategory).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Update category with non-existing ID should throw exception")
    void update_ValidUpdateCategory_WithNonExistingId_ShouldThrowEntityNotFoundException() {
        Long categoryId = 999L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("Non-existent category",
                "Non-existent category description");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));
    }

    @Test
    @DisplayName("Get books by category ID should return list of BookDtoWithoutCategoryIds")
    void getBooksByCategoryId_ShouldReturnBooks() {
        Long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> books = createBooksList();

        when(bookRepository.findByCategoriesId(categoryId)).thenReturn(books);

        List<BookDtoWithoutCategoryIds> result = categoryService.getBooksByCategoryId(categoryId);

        assertThat(result).isEqualTo(books);
    }

    private List<BookDtoWithoutCategoryIds> createBooksList() {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                1L,
                "Book Title",
                "Author Name",
                "ISBN11111",
                new BigDecimal("99.99"),
                "Description of the book",
                "CoverImageUrl"
        );
        return List.of(bookDto);
    }
}
