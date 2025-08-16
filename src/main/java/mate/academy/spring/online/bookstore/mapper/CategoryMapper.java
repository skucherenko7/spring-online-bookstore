package mate.academy.spring.online.bookstore.mapper;

import mate.academy.spring.online.bookstore.config.MapperConfig;
import mate.academy.spring.online.bookstore.dto.category.CategoryDto;
import mate.academy.spring.online.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.spring.online.bookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryRequestDto requestDto);

    void updateCategoryFromDto(@MappingTarget Category category,
                               CreateCategoryRequestDto requestDto);
}
