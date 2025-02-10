package mate.academy.spring.online.bookstore;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringOnlineBookstoreApplication {

    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(SpringOnlineBookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Kobzar");
            book.setAuthor("Taras Shevchenko");
            book.setIsbn("123456789");
            book.setPrice(new BigDecimal(100.98));
            book.setDescription("This book is the best");
            book.setCoverImage("cover.jpg");
            bookService.save(book);

            System.out.println("Book saved" + book.getTitle());
        };
    }
}
