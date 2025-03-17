package mate.academy.spring.online.bookstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.spring.online.bookstore.validation.Isbn;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateBookRequestDto {

    @NotBlank(message = "Title is required! Please, enter a title!")
    private String title;

    @NotBlank(message = "Author is required! Please, enter an author!")
    private String author;

    @NotBlank(message = "ISBN is required! Please, enter a valid ISBN number!")
    @Size(min = 10, max = 14, message = "ISBN must be between 10 and 14 characters!")
    @Isbn(message = "Invalid ISBN")
    private String isbn;

    @NotNull(message = "Price is required! Please, enter a price!")
    @DecimalMin(value = "0.01", message = "The specified price must be greater than 0!")
    private BigDecimal price;

    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;

    @URL(message = "Invalid cover image URL! Please, enter a valid URL!")
    private String coverImage;

    private boolean deleted;
}
