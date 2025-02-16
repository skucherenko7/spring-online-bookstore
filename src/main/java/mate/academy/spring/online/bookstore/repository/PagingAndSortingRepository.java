package mate.academy.spring.online.bookstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface PagingAndSortingRepository<T, IdentifierT> extends Repository<T, IdentifierT> {

    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);
}
