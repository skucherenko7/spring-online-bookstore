package mate.academy.spring.online.bookstore.repository.category;

import mate.academy.spring.online.bookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
