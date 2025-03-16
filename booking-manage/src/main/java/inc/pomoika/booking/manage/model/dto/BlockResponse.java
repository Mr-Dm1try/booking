package inc.pomoika.booking.manage.model.dto;

import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.dto.DateRange;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockResponse {
    private Long id;
    private Long propertyId;
    private DateRange dateRange;

    public static BlockResponse from(Block block) {
        return BlockResponse.builder()
                .id(block.getId())
                .propertyId(block.getPropertyId())
                .dateRange(new DateRange(block.getStartDate(), block.getEndDate()))
                .build();
    }
} 