package inc.pomoika.booking.read.model.dto;

import inc.pomoika.booking.common.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingReadResponse {
    private long id;
    private long propertyId;
    private long guestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 