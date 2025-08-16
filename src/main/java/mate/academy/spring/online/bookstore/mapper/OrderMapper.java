package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toDto(Order order);

    Order toEntity(CreateOrderRequestDto createOrderRequestDto);
}
