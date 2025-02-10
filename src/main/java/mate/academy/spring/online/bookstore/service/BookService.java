package mate.academy.spring.online.bookstore.service;

import java.util.List;
import mate.academy.spring.online.bookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
