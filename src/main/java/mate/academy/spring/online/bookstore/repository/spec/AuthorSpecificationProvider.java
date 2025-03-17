package mate.academy.spring.online.bookstore.repository.spec;

import static mate.academy.spring.online.bookstore.repository.book.BookSpecificationBuilder.AUTHOR_PARAMETER;

import java.util.Arrays;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return AUTHOR_PARAMETER;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(AUTHOR_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
