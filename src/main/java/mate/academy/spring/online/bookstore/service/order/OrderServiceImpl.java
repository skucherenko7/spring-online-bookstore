package mate.academy.spring.online.bookstore.service.order;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.exception.OrderProcessingException;
import mate.academy.spring.online.bookstore.mapper.OrderItemMapper;
import mate.academy.spring.online.bookstore.mapper.OrderMapper;
import mate.academy.spring.online.bookstore.model.Order;
import mate.academy.spring.online.bookstore.model.Order.Status;
import mate.academy.spring.online.bookstore.model.OrderItem;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.repository.order.OrderItemRepository;
import mate.academy.spring.online.bookstore.repository.order.OrderRepository;
import mate.academy.spring.online.bookstore.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderResponseDto save(User user, CreateOrderRequestDto createOrderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId());

        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException("Shoppingcart is empty! You can't place an order.",
                    new Exception("Empty cart"));
        }

        Order order = createOrder(user, createOrderRequestDto);
        order.setTotal(calculateTotal(shoppingCart));
        List<OrderItem> orderItems = createOrderItems(order, shoppingCart);
        order.setOrderItems(new HashSet<>(orderItems));
        orderRepository.save(order);
        shoppingCart.clear();
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemResponseDto> getOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: "
                        + orderId + " and User ID: " + userId));

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public UpdateOrderStatusRequestDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto
            updateOrderStatusRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not "
                        + "found with ID: " + orderId));
        Status newStatus = updateOrderStatusRequestDto.status();
        order.setStatus(newStatus);
        orderRepository.save(order);
        return new UpdateOrderStatusRequestDto(order.getStatus());
    }

    @Override
    public OrderResponseDto findById(Long id) {
        return orderMapper.toDto(
                orderRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Order "
                                + "not found with ID: " + id))
        );
    }

    @Override
    public List<OrderResponseDto> findAll(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId, Long userId) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrder_IdAndOrder_User_Id(itemId, orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with ID: "
                        + itemId + ", Order ID: " + orderId + " and User ID: " + userId));
        return orderItemMapper.toDto(orderItem);
    }

    private Order createOrder(User user, CreateOrderRequestDto createOrderRequestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(createOrderRequestDto.getShippingAddress());
        return order;
    }

    private BigDecimal calculateTotal(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> cartItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItems(Order order, ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .toList();
    }
}
