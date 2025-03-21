package mate.academy.spring.online.bookstore.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import mate.academy.spring.online.bookstore.dto.user.UserRegistrationRequestDto;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto dto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(dto.getPassword(), dto.getRepeatPassword());
    }
}
