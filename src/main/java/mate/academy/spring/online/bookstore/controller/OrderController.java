package mate.academy.spring.online.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.User;
import mate.academy.spring.online.bookstore.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders management", description = "Endpoints for orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get a user's order history",
            description = "Retrieve the list of orders placed by a user")
    public List<OrderResponseDto> getUserOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.findAll(user, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place a new order by a user from the shopping cart")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(
            Authentication authentication,
            @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.save(user, createOrderRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all items in a specific order",
            description = "Show all items that are part of a specific order for the current user")
    public List<OrderItemResponseDto> getOrderItems(@PathVariable Long orderId,
                                                    @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return orderService.getOrderItems(orderId, userId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get a specific order item",
            description = "Retrieve a specific OrderItem within an order")
    public ResponseEntity<OrderItemResponseDto> getOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        OrderItemResponseDto orderItemResponse = orderService.getOrderItem(orderId, itemId, userId);
        return ResponseEntity.ok(orderItemResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status", description = "Update "
            + "the status of an order by its ID")
    public ResponseEntity<UpdateOrderStatusRequestDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequestDto updateOrderStatusRequestDto) {
        UpdateOrderStatusRequestDto updatedOrder = orderService.updateOrderStatus(orderId,
                updateOrderStatusRequestDto);
        return ResponseEntity.ok(updatedOrder);
    }
}
