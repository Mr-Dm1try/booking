package inc.pomoika.booking.common.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class BookingOverlapException extends BookingException {
    private final List<Long> bookingIds;

    public BookingOverlapException(String message, List<Long> bookingIds) {
        super(message);
        this.bookingIds = bookingIds;
    }
} 