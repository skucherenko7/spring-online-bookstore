package mate.academy.spring.online.bookstore.exception;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message, Exception e) {
        super(message);
    }
}
