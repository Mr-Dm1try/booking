package inc.pomoika.booking.common.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

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

    public static ErrorResponse ofCancelledBooking(String message, long bookingId) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.CANCELLED_BOOKING.name())
                .params(Map.of("id", bookingId))
                .build();
    }

    public static ErrorResponse notFound(String message) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.NOT_FOUND.name())
                .build();
    }

    public static ErrorResponse of(String message) {
        return ErrorResponse.builder()
                .message(message)
                .code(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .build();
    }
} 