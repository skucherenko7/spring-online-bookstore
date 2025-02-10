package mate.academy.spring.online.bookstore.repository;

import java.util.List;
import mate.academy.spring.online.bookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
