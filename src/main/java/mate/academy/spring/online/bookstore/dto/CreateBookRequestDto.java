package mate.academy.spring.online.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.spring.online.bookstore.validation.Isbn;
import org.hibernate.validator.constraints.URL;

@Data
@Schema(description = "Request DTO for creating a new book.")
public class CreateBookRequestDto {

    @Schema(description = "The title of the book.", example = "Kobzar")
    @NotBlank(message = "Title is required! Please, enter a title!")
    private String title;

    @Schema(description = "The author of the book.", example = "Taras Shevchenko")
    @NotBlank(message = "Author is required! Please, enter an author!")
    private String author;

    @Schema(description = "The ISBN number of the book.", example = "978-1234567111")
    @NotBlank(message = "ISBN is required! Please, enter a valid ISBN number!")
    @Size(min = 10, max = 14, message = "ISBN must be between 10 and 14 characters!")
    @Isbn(message = "Invalid ISBN")
    private String isbn;

    @Schema(description = "The price of the book.", example = "300.00")
    @NotNull(message = "Price is required! Please, enter a price!")
    @DecimalMin(value = "0.01", message = "The specified price must be greater than 0!")
    private BigDecimal price;

    @Schema(description = "A short description of the book's content",
            example = "Сollection of poetry.")
    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;

    @Schema(description = "URL to the cover image of the book",
            example = "https://example.com/updated-cover-image.jpg")
    @URL(message = "Invalid cover image URL! Please, enter a valid URL!")
    private String coverImage;

    @Schema(description = "Indicates if the entity has been deleted.", example = "false")
    private boolean deleted;
}
