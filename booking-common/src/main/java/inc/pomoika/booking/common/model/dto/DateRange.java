package inc.pomoika.booking.common.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public boolean isValid() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }

    public boolean overlaps(DateRange other) {
        return !startDate.isAfter(other.endDate) && !endDate.isBefore(other.startDate);
    }
} 