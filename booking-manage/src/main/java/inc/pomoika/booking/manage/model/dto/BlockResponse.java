package inc.pomoika.booking.manage.model.dto;

import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.dto.DateRange;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BlockResponse {
    long id;
    long propertyId;
    DateRange dateRange;

    public static BlockResponse of(Block block) {
        return BlockResponse.builder()
                .id(block.getId())
                .propertyId(block.getPropertyId())
                .dateRange(new DateRange(block.getStartDate(), block.getEndDate()))
                .build();
    }
} 