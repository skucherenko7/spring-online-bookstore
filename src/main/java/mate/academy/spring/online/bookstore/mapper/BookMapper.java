package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.BookDto;
import mate.academy.spring.online.bookstore.dto.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
