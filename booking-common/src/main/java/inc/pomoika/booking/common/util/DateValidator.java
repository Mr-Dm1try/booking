package inc.pomoika.booking.common.util;

import lombok.experimental.UtilityClass;
import inc.pomoika.booking.common.exception.BookingValidationException;

import java.time.LocalDate;

@UtilityClass
public class DateValidator {
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BookingValidationException("Start date and end date must not be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new BookingValidationException("End date must be after start date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new BookingValidationException("Start date must be in the future");
        }
    }

    public static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
} 