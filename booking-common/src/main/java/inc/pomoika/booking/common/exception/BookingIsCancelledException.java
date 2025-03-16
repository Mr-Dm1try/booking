package inc.pomoika.booking.common.exception;

import lombok.Getter;

@Getter
public class BookingIsCancelledException extends RuntimeException {
    private final long bookingId;

    public BookingIsCancelledException(String message, long bookingId) {
        super(message);
        this.bookingId = bookingId;
    }
} 