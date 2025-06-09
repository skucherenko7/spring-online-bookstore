package mate.academy.spring.online.bookstore.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import mate.academy.spring.online.bookstore.dto.orderitem.CreateOrderItemRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.Book;
import mate.academy.spring.online.bookstore.model.Order;
import mate.academy.spring.online.bookstore.model.OrderItem;

public class OrderItemUtil {
    public static CreateOrderItemRequestDto createOrderItemRequestDto() {
        return new CreateOrderItemRequestDto(7L, 2);
    }

    public static OrderItem createOrderItem(CreateOrderItemRequestDto requestDto,
                                            Order order, Book book) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(book);
        orderItem.setQuantity(requestDto.quantity());
        orderItem.setOrder(order);
        return orderItem;
    }

    public static OrderItem getOrderItem() {
        Order order = new Order();
        order.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Example Book");

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setOrder(order);
        orderItem.setPrice(book.getPrice() != null ? book.getPrice() : BigDecimal.valueOf(100));

        return orderItem;
    }

    public static OrderItemResponseDto createOrderItemResponseDto(OrderItem orderItem) {
        return new OrderItemResponseDto(
                orderItem.getId(),
                orderItem.getBook().getId(),
                orderItem.getQuantity()
        );
    }

    public static void verifyOrderItemResponseDto(OrderItemResponseDto expected,
                                                  OrderItemResponseDto actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.bookId(), actual.bookId());
        assertEquals(expected.quantity(), actual.quantity());
    }

    public static CreateOrderItemRequestDto getInvalidOrderItemRequestDto() {
        return new CreateOrderItemRequestDto(null, -1);
    }
}
