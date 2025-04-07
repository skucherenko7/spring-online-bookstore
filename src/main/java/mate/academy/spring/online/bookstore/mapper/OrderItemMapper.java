package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
