package mate.academy.spring.online.bookstore.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.spring.online.bookstore.dto.UserRegistrationRequestDto;

import java.util.Objects;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto dto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }
        return Objects.equals(dto.getPassword(), dto.getRepeatPassword());
    }
}
