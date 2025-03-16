package inc.pomoika.booking.common.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    String message;
    String code;
    Map<String, Object> params;

    public static ErrorResponse validationError(String message) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.VALIDATION_ERROR.name())
                .build();
    }

    public static ErrorResponse ofBlocks(String message, List<Long> blockIds) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.BLOCK_OVERLAP.name())
                .params(Map.of("ids", blockIds))
                .build();
    }

    public static ErrorResponse ofBookings(String message, List<Long> bookingIds) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.BOOKING_OVERLAP.name())
                .params(Map.of("ids", bookingIds))
                .build();
    }
} 