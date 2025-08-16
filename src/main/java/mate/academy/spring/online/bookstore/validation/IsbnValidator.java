package mate.academy.spring.online.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {

    private static final String ISBN_13_PATTERN = "^(978|979)\\d{10}$";
    private static final String ISBN_10_PATTERN = "^(\\d{9}[0-9X])$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null) {
            return false;
        }

        String cleanIsbn = s.replaceAll("-", "");

        return cleanIsbn.matches(ISBN_13_PATTERN) || cleanIsbn.matches(ISBN_10_PATTERN);
    }
}


