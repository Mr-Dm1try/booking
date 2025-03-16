package inc.pomoika.booking.common.exception;

public class BookingValidationException extends BookingException {
    public BookingValidationException(String message) {
        super(message);
    }

    public BookingValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 