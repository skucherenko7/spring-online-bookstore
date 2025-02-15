package mate.academy.spring.online.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final String ISBN_PATTERN =
            "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$";

    @Override
    public boolean isIsbnValid(String s, ConstraintValidatorContext context) {
        if (s == null) {
            return false;
        }
        boolean isIsbnValid = s.matches(ISBN_PATTERN);
        return isIsbnValid;
    }
}
