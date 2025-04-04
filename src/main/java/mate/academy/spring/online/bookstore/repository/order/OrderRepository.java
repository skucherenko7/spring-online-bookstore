package mate.academy.spring.online.bookstore.repository.order;

import mate.academy.spring.online.bookstore.model.Order;
import mate.academy.spring.online.bookstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
}
