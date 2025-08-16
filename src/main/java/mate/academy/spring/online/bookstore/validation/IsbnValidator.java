package mate.academy.spring.online.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {

    // Регулярний вираз для ISBN-13 і ISBN-10 з дефісами та без них
    private static final String ISBN_13_PATTERN = "^(978|979)\\d{10}$";
    private static final String ISBN_10_PATTERN = "^(\\d{9}[0-9X])$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null) {
            return false;
        }

        // Видаляємо всі дефіси, щоб працювати з чистими числами
        String cleanIsbn = s.replaceAll("-", "");

        // Перевіряємо, чи ISBN підходить під один з патернів
        return cleanIsbn.matches(ISBN_13_PATTERN) || cleanIsbn.matches(ISBN_10_PATTERN);
    }
}


