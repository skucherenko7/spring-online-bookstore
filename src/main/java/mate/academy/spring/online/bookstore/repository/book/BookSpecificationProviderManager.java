package mate.academy.spring.online.bookstore.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.exception.SpecificationException;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.SpecificationProvider;
import mate.academy.spring.online.bookstore.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationException("Can`t find correct specification"
                        + "provider for key: " + key));
    }
}
