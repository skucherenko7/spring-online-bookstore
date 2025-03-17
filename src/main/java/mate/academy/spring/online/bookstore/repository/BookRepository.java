package mate.academy.spring.online.bookstore.repository;

import mate.academy.spring.online.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
