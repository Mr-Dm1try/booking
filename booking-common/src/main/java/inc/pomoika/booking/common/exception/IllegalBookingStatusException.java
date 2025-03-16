package inc.pomoika.booking.common.exception;

import lombok.Getter;

@Getter
public class IllegalBookingStatusException extends BookingException {
    private final long bookingId;
    private final String status;
    public IllegalBookingStatusException(String message, long bookingId, String status) {
        super(message);
        this.bookingId = bookingId;
        this.status = status;
    }
} 