package mate.academy.spring.online.bookstore.service.order;

import java.util.List;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.User;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto save(User user, CreateOrderRequestDto createOrderRequestDto);

    List<OrderItemResponseDto> getOrderItems(Long orderId, Long userId);

    UpdateOrderStatusRequestDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto
            updateOrderStatusRequestDto);

    OrderResponseDto findById(Long id);

    List<OrderResponseDto> findAll(User user, Pageable pageable);

    OrderItemResponseDto getOrderItem(Long orderId, Long itemId, Long userId);
}
