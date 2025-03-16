package inc.pomoika.booking.manage.model.dto;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.validation.ValidDateRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BlockRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Date range is required")
    @Valid
    @ValidDateRange
    private DateRange dateRange;
} 