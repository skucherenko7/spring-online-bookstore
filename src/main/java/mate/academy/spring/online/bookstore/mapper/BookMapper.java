package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.book.BookDto;
import mate.academy.spring.online.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.spring.online.bookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
