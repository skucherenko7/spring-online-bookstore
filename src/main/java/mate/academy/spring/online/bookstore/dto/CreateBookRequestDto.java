package mate.academy.spring.online.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String isbn;

    private BigDecimal price;

    private String description;

    private String coverImage;

    private boolean deleted;
}
