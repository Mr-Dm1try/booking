package inc.pomoika.booking.create.model.dto;

import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import lombok.Value;

@Value
public class BookingResponse {
    long id;
    long propertyId;
    long guestId;
    DateRange dateRange;
    BookingStatus status;
} 