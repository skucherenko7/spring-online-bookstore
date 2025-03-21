package mate.academy.spring.online.bookstore.dto.book;

public record BookSearchParametersDto(String[] title, String[] author, String[] isbn) {
}
