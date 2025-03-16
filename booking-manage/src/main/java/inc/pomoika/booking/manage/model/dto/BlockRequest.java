package inc.pomoika.booking.manage.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.validation.ValidDateRange;

@Value
@Builder
public class BlockRequest {
    @NotNull(message = "Property ID is required")
    Long propertyId;

    @NotNull(message = "Date range is required")
    @Valid
    @ValidDateRange
    DateRange dateRange;
} 