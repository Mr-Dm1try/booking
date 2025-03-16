package inc.pomoika.booking.create.model.dto;

import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationResponse {
    private long id;
    private long propertyId;
    private long guestId;
    private DateRange dateRange;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 