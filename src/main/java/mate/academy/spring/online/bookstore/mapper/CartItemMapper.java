package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemDto;
import mate.academy.spring.online.bookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.online.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem item);

    // Тепер маппер працює безпосередньо з DTO і об'єктами
    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookById")
    CartItem toModel(CartItemRequestDto requestDto);
}
