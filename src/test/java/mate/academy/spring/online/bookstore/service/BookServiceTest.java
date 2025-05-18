package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.book.BookDto;
import mate.academy.spring.online.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.spring.online.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.mapper.BookMapper;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.book.BookRepository;
import mate.academy.spring.online.bookstore.repository.book.BookSpecificationBuilder;
import mate.academy.spring.online.bookstore.repository.category.CategoryRepository;
import mate.academy.spring.online.bookstore.service.book.BookServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;


    @Test
    @DisplayName("Save book with valid categories should return BookDto")
    void saveBook_WithValidCategories_ShouldReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Kateryna")
                .setAuthor("Shevchenko")
                .setIsbn("978-1434567111")
                .setPrice(BigDecimal.valueOf(240.00))
                .setDescription("Updated description")
                .setCoverImage("https://example.com/updated-cover-image.jpg")
                .setCategories(Set.of(1L));

        Book book = new Book()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage());

        BookDto bookDto = new BookDto()
                .setId(1L)
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor());

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.saveBook(requestDto);

        assertThat(result).isEqualTo(bookDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Save book with invalid categories should throw EntityNotFoundException")
    void saveBook_WithInvalidCategories_ShouldThrowException() {
        Long nonExistingCategoryId = 99L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Invalid Book")
                .setAuthor("Author")
                .setIsbn("123456789")
                .setCategories(Set.of(nonExistingCategoryId));

        when(categoryRepository.existsById(nonExistingCategoryId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.saveBook(requestDto)
        );

        assertEquals(
                "Categories do not exist: [99]",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Save book with empty categories should throw EntityNotFoundException")
    void saveBook_WithEmptyCategories_ShouldThrowException() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book with No Categories")
                .setAuthor("Author")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(99.99))
                .setCategories(Set.of());

        assertThrows(EntityNotFoundException.class, () -> bookService.saveBook(requestDto));
    }

    @Test
    @DisplayName("Get book by ID should return BookDto")
    void getBookById_ShouldReturnBookDto() {
        Long id = 1L;
        Book book = new Book().setId(id).setTitle("Test Book");
        BookDto bookDto = new BookDto().setId(id).setTitle("Test Book");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.getBookById(id);

        assertThat(result).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Get book by non-existing ID should throw EntityNotFoundException")
    void getBookById_NonExisting_ShouldThrowException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(999L));
    }

    @Test
    @DisplayName("Find all books should return Page<BookDto>")
    void findAll_ShouldReturnBookPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Book book = new Book().setId(1L).setTitle("Test");
        BookDto bookDto = new BookDto().setId(1L).setTitle("Test");
        Page<Book> books = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.findAll(pageable);

        assertThat(result.getContent()).containsExactly(bookDto);
    }

    @Test
    @DisplayName("Delete book by ID should call repository")
    void deleteBookById_ShouldDelete() {
        Long id = 1L;

        lenient().when(bookRepository.existsById(id)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(id);

        bookService.deleteBookById(id);

        verify(bookRepository).deleteById(id);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book with valid ID should return BookDto")
    void updateBook_ValidId_ShouldReturnUpdatedDto() {
        Long id = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Updated Book")
                .setAuthor("Author Updated")
                .setIsbn("978-1234567890")
                .setPrice(BigDecimal.valueOf(99.99))
                .setCategories(Set.of(1L));

        Book book = new Book().setId(id).setTitle("Old Title");
        BookDto updatedDto = new BookDto().setId(id).setTitle("Updated Book");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(categoryRepository.existsById(1L)).thenReturn(true);

        doNothing().when(bookMapper).updateBookFromDto(requestDto, book);

        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(updatedDto);

        BookDto result = bookService.updateBook(id, requestDto);

        assertThat(result).isEqualTo(updatedDto);
    }

    @Test
    @DisplayName("Update book with invalid ID should throw EntityNotFoundException")
    void updateBook_InvalidId_ShouldThrow() {
        Long id = 999L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(id, requestDto));
    }

    @Test
    @DisplayName("Update book with invalid data should throw EntityNotFoundException")
    void updateBook_WithInvalidData_ShouldThrowException() {
        Long id = 999L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(id, requestDto));
    }

    @Test
    @DisplayName("Search books with empty parameters should return empty page")
    void search_EmptyParameters_ShouldReturnEmptyPage() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{},
                new String[]{},
                new String[]{}
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> books = new PageImpl<>(List.of());

        Specification<Book> specification = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(eq(specification), eq(pageable))).thenReturn(books);

        Page<BookDto> result = bookService.search(params, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Search books should return matching BookDto page")
    void search_ShouldReturnMatchingBooks() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Test"}, new String[]{"Author"}, new String[]{"123"}
        );
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Book> specification = mock(Specification.class);

        Book book = new Book().setId(1L).setTitle("Test");
        BookDto bookDto = new BookDto().setId(1L).setTitle("Test");

        Page<Book> books = new PageImpl<>(List.of(book));

        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.search(params, pageable);

        assertThat(result.getContent()).containsExactly(bookDto);
    }
}
