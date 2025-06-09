package mate.academy.spring.online.bookstore.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.Order;
import mate.academy.spring.online.bookstore.model.Order.Status;

public class OrderUtil {

    public static OrderResponseDto getOrderResponseDto(Long orderId, Long userId,
                                                       Set<OrderItemResponseDto> items,
                                                       BigDecimal total,
                                                       String status) {
        return new OrderResponseDto(
                orderId,
                userId,
                items,
                LocalDateTime.now(),
                total,
                status
        );
    }

    public static OrderResponseDto getSampleOrderResponseDto() {
        Set<OrderItemResponseDto> items = Set.of(
                getOrderItemResponseDto(1L, 101L, 2),
                getOrderItemResponseDto(2L, 102L, 1)
        );

        return getOrderResponseDto(1L, 1L, items, BigDecimal.valueOf(80.00), "PENDING");
    }

    public static OrderItemResponseDto getOrderItemResponseDto(Long id, Long bookId, int quantity) {
        return new OrderItemResponseDto(id, bookId, quantity);
    }

    public static CreateOrderRequestDto getOrderRequestDto() {
        return new CreateOrderRequestDto("145 Main St, City, Country");
    }

    public static UpdateOrderStatusRequestDto getOrderUpdateDto() {
        return new UpdateOrderStatusRequestDto(Status.DELIVERED);
    }

    public static List<Order> getListOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setTotal(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_UP));
        order1.setStatus(Status.PENDING);
        order1.setShippingAddress("testShippingAddress");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotal(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP));
        order2.setStatus(Status.COMPLETED);
        order2.setShippingAddress("TEST ADDRESS 1");

        return List.of(order1, order2);
    }

    public static Order getOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(UserUtil.getUserAfterSaveIntoDb());
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(BigDecimal.valueOf(40));
        order.setStatus(Status.PENDING);
        order.setOrderItems(Set.of(OrderItemUtil.getOrderItem()));
        order.setShippingAddress("Test shipping address");

        return order;
    }

    public static OrderResponseDto getNewOrderResponseDto() {

        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto(4L, 1L, 20);
        return new OrderResponseDto(
                4L,
                1L,
                Set.of(orderItemResponseDto),
                LocalDateTime.now(),
                BigDecimal.valueOf(3000),
                "PENDING"
        );
    }

    public static List<OrderResponseDto> getListResponseDto() {
        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto(1L, 1L, 20);

        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                1L,
                Set.of(orderItemResponseDto),
                LocalDateTime.now(),
                BigDecimal.valueOf(500),
                "PENDING"
        );

        return List.of(orderResponseDto);
    }
}
