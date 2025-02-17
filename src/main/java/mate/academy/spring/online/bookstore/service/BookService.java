package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.BookDto;
import mate.academy.spring.online.bookstore.dto.BookSearchParametersDto;
import mate.academy.spring.online.bookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto saveBook(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    Page<BookDto> findAll(Pageable pageable);

    void deleteBookById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);

    Page<BookDto> search(BookSearchParametersDto params, Pageable pageable);

}
