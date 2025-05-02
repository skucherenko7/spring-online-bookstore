package mate.academy.spring.online.bookstore.example;

import java.math.BigDecimal;
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

public class BookUtil {

    public static CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Eat, pray, love");
        requestDto.setAuthor("Elizabeth Gilbert");
        requestDto.setIsbn("978-1234563662");
        requestDto.setPrice(BigDecimal.valueOf(350));
        requestDto.setDescription("Women’s novels");
        requestDto.setCoverImage("Updated description");
        requestDto.setCategories(Set.of(7L));
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
        category.setId(7L);
        category.setName("Novel");
        category.setDescription("Women’s novels");
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
        bookDto.setCategoriesIds(List.of(7L));
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
        BookDto expectedDto = new BookDto();
        expectedDto.setId(testId);
        expectedDto.setTitle("Seven Husbands of Evelyn Hugo");
        expectedDto.setAuthor("Taylor Jenkins Reid");
        expectedDto.setIsbn("978-1234567989");
        expectedDto.setDescription("Updated description");
        expectedDto.setCoverImage("https://example.com/updated-cover-image.jpg");
        expectedDto.setCategoriesIds(List.of(5L, 7L));
        expectedDto.setPrice(new BigDecimal("520.00"));
        return expectedDto;
    }

    public static CreateBookRequestDto creatBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Kobzar");
        requestDto.setAuthor("Shevchenko");
        requestDto.setIsbn("978-1234561999");
        requestDto.setDescription("Updated description");
        requestDto.setCoverImage("https://example.com/updated-cover-image.jpg");
        requestDto.setCategories(Set.of(2L));
        requestDto.setPrice(BigDecimal.valueOf(1200.00));
        return requestDto;
    }

    public static BookDto createExpectedBookDto(Long id) {
        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle("Kobzar");
        expectedDto.setAuthor("Shevchenko");
        expectedDto.setIsbn("978-1234561999");
        expectedDto.setDescription("Updated description");
        expectedDto.setCoverImage("https://example.com/updated-cover-image.jpg");
        expectedDto.setCategoriesIds(List.of(2L));
        expectedDto.setPrice(BigDecimal.valueOf(1200.00));
        return expectedDto;
    }

    public static CreateBookRequestDto getInvalidCreateBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("title");
        requestDto.setAuthor("author");
        requestDto.setIsbn("isbn");
        requestDto.setDescription("description");
        requestDto.setCoverImage("coverImage");
        requestDto.setCategories(Set.of());
        requestDto.setPrice(BigDecimal.valueOf(-5));
        return requestDto;
    }
}
