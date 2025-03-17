package mate.academy.spring.online.bookstore.repository.spec;

import static mate.academy.spring.online.bookstore.repository.book.BookSpecificationBuilder.TITLE_PARAMETER;

import java.util.Arrays;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return TITLE_PARAMETER;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(TITLE_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
