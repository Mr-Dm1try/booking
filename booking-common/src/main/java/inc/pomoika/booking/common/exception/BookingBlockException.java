package inc.pomoika.booking.common.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class BookingBlockException extends BookingException {
    private final List<Long> blockIds;

    public BookingBlockException(String message, List<Long> blockIds) {
        super(message);
        this.blockIds = blockIds;
    }
} 