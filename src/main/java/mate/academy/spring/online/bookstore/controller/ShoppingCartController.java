package mate.academy.spring.online.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "BearerAuth")
@Tag(name = "ShoppingCart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get the shopping cart", description = "Get the shopping cart by id")
    public ShoppingCartDto getShoppingCartByUser(Authentication authentication) {
        return shoppingCartService
                .getShoppingCartByUserId(authentication);
    }

    @PostMapping
    @Operation(summary = "Add a book", description = "Add book to the shopping cart")
    public ShoppingCartDto save(@RequestBody @Valid CartItemRequestDto requestDto,
                                Authentication authentication) {
        return shoppingCartService
                .addCartItem(requestDto, authentication);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update the cart item", description = "Update the cart item by id")
    public ShoppingCartDto update(@PathVariable Long id,
                                  @RequestBody @Valid UpdateCartItemRequestDto
                                          updateDto, Authentication authentication) {
        return shoppingCartService.updateCartItemById(id,
                updateDto, authentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete the cart item", description = "Delete the cart item by id")
    public void delete(@PathVariable Long id) {
        shoppingCartService.deleteCartItemById(id);
    }
}
