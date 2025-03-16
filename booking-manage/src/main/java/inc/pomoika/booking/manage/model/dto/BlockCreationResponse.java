package inc.pomoika.booking.manage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockCreationResponse {
    private long id;
    private long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
} 