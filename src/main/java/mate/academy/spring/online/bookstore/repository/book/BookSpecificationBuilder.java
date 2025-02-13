package mate.academy.spring.online.bookstore.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.BookSearchParametersDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.repository.SpecificationBuilder;
import mate.academy.spring.online.bookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    public static final String AUTHOR_PARAMETER = "author";
    public static final String ISBN_PARAMETER = "isbn";
    public static final String TITLE_PARAMETER = "title";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> bookSpecification = Specification.where(null);
        if (searchParameters.author() != null && searchParameters.author().length > 0) {
            bookSpecification = bookSpecification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(AUTHOR_PARAMETER)
                            .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && searchParameters.isbn().length > 0) {
            bookSpecification = bookSpecification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(ISBN_PARAMETER)
                            .getSpecification(searchParameters.isbn()));
        }
        if (searchParameters.title() != null && searchParameters.title().length > 0) {
            bookSpecification = bookSpecification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(TITLE_PARAMETER)
                            .getSpecification(searchParameters.title()));
        }
        return bookSpecification;
    }
}
