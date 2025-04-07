package mate.academy.spring.online.bookstore.repository.order;

import java.util.Optional;
import mate.academy.spring.online.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByIdAndOrder_IdAndOrder_User_Id(Long itemId, Long orderId, Long userId);
}


