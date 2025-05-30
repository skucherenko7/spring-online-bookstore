package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.online.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class})
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
