package mate.academy.spring.online.bookstore.example;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mate.academy.spring.online.bookstore.dto.book.BookDto;
import mate.academy.spring.online.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookUtil {

    public static CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Eat, pray, love");
        requestDto.setAuthor("Elizabeth Gilbert");
        requestDto.setIsbn("978-1234563662");
        requestDto.setPrice(BigDecimal.valueOf(350.00));
        requestDto.setDescription("Women’s novels");
        requestDto.setCoverImage("Updated description");
        requestDto.setCategories(Set.of(5L));

        return requestDto;
    }

    public static Book createBook(CreateBookRequestDto requestDto) {
        Book book = new Book();
        book.setId(1L);
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());

        Category category = new Category();
        category.setId(1L);
        category.setName("Non fiction book");
        category.setDescription("Description");
        category.setIsDeleted(false);
        book.setCategories(Set.of(category));

        return book;
    }

    public static BookDto createBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoriesIds(List.of(1L));
        return bookDto;
    }

    public static Pageable createPageable() {
        return PageRequest.of(0, 10, Sort.by("title"));
    }

    public static Page<Book> createBookPage(Pageable pageable) {
        Book book = createBook(createBookRequestDto());
        return new PageImpl<>(List.of(book), pageable, 1);
    }

    public static BookDto getBookDto(Long testId) {
        String testTitle = "Seven Husbands of Evelyn Hugo";
        String testAuthor = "Taylor Jenkins Reid";
        String testIsbn = "978-1234567989";
        String testDescription = "Updated description";
        String testCoverImage = "https://example.com/updated-cover-image.jpg";
        List<Long> testCategoryIds = List.of(3L, 5L);
        BigDecimal testPrice = new BigDecimal("520.00");

        BookDto expectedDto = new BookDto();
        expectedDto.setId(testId);
        expectedDto.setTitle(testTitle);
        expectedDto.setAuthor(testAuthor);
        expectedDto.setIsbn(testIsbn);
        expectedDto.setDescription(testDescription);
        expectedDto.setCoverImage(testCoverImage);
        expectedDto.setCategoriesIds(testCategoryIds);
        expectedDto.setPrice(testPrice);
        return expectedDto;
    }

    public static CreateBookRequestDto creatBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Eat, pray, love");
        requestDto.setAuthor("Elizabeth Gilbert");
        requestDto.setIsbn("978-1234563662");
        requestDto.setDescription("Women’s novels");
        requestDto.setCoverImage("https://example.com/updated-cover-image.jpg");
        requestDto.setCategories(Set.of(1L));
        requestDto.setPrice(BigDecimal.valueOf(1500.00));
        return requestDto;
    }

    public static BookDto createExpectedBookDto(Long id) {
        String title = "Eat, pray, love";
        String author = "Elizabeth Gilbert";
        String isbn = "978-1234563662";
        String description = "Women’s novels";
        String coverImage = "https://example.com/updated-cover-image.jpg";
        List<Long> categoryIds = List.of(1L);
        BigDecimal price = BigDecimal.valueOf(1500.00);

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle(title);
        expectedDto.setAuthor(author);
        expectedDto.setIsbn(isbn);
        expectedDto.setDescription(description);
        expectedDto.setCoverImage(coverImage);
        expectedDto.setCategoriesIds(categoryIds);
        expectedDto.setPrice(price);
        return expectedDto;
    }

    public static CreateBookRequestDto getInvalidCreateBookRequestDto() {
        String testTitle = "title";
        String testAuthor = "author";
        String testIsbn = "isbn";
        String description = "description";
        String coverImage = "coverImage";
        Set<Long> testCategoryIds = Set.of(2L);
        BigDecimal testPrice = BigDecimal.valueOf(-5);

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setTitle(testTitle);
        createBookRequestDto.setAuthor(testAuthor);
        createBookRequestDto.setIsbn(testIsbn);
        createBookRequestDto.setDescription(description);
        createBookRequestDto.setCoverImage(coverImage);
        createBookRequestDto.setCategories(testCategoryIds);
        createBookRequestDto.setPrice(testPrice);

        return createBookRequestDto;
    }

    public static void verifyBookDtoEquality(BookDto expected, BookDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(
                new HashSet<>(expected.getCategoriesIds()),
                new HashSet<>(actual.getCategoriesIds()),
                "Categories IDs should match regardless of order"
        );

        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(expected.getDescription(), actual.getDescription());
    }
}
