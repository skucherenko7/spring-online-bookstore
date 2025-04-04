package mate.academy.spring.online.bookstore.repository.order;

import mate.academy.spring.online.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
