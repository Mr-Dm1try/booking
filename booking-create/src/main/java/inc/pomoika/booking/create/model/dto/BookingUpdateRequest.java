package inc.pomoika.booking.create.model.dto;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookingUpdateRequest {
    @NotNull
    @Positive
    long guestId;

    @NotNull
    @ValidDateRange
    DateRange dateRange;
} 