package mate.academy.spring.online.bookstore.dto;

public record BookSearchParametersDto(String[] title, String[] author, String[] isbn) {
}
