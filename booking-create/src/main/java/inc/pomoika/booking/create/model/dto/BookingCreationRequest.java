package inc.pomoika.booking.create.model.dto;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationRequest {
    @NotNull(message = "Property ID is required")
    @Positive(message = "Property ID must be positive")
    private long propertyId;

    @NotNull(message = "Guest ID is required")
    @Positive(message = "Guest ID must be positive")
    private long guestId;

    @NotNull(message = "Date range is required")
    @ValidDateRange
    private DateRange dateRange;
} 