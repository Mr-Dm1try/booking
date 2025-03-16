package inc.pomoika.booking.create.model.dto;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.validation.ValidDateRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingUpdateRequest {
    @NotNull
    private Long guestId;

    @NotNull
    @Valid
    @ValidDateRange
    private DateRange dateRange;
} 