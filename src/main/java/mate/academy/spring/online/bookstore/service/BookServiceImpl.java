package mate.academy.spring.online.bookstore.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.BookDto;
import mate.academy.spring.online.bookstore.dto.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.mapper.BookMapper;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.toModel(bookRequestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findBookById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id" + id + " not found.")
        );
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
