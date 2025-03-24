package mate.academy.spring.online.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequestDto(
        @NotBlank
        String name,
        @Size(max = 1000)
        String description
) {
}
