package mate.academy.spring.online.bookstore.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.spring.online.bookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    Optional<Book> findBookById(Long id);

    List<Book> findAll();
}
