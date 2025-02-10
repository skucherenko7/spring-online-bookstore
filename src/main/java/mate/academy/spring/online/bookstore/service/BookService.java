package mate.academy.spring.online.bookstore.service;

import java.util.List;
import mate.academy.spring.online.bookstore.dto.BookDto;
import mate.academy.spring.online.bookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    List<BookDto> findAll();
}
