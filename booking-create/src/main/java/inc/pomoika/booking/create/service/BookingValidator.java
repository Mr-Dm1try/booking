package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.exception.BookingBlockException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public void validateBooking(long propertyId, DateRange dateRange) {
        validateNoOverlappingBookings(propertyId, dateRange, null);
        validateNoOverlappingBlocks(propertyId, dateRange);
    }

    public void validateBookingUpdate(long propertyId, DateRange dateRange, Long excludeBookingId) {
        validateNoOverlappingBookings(propertyId, dateRange, excludeBookingId);
        validateNoOverlappingBlocks(propertyId, dateRange);
    }

    private void validateNoOverlappingBookings(long propertyId, DateRange dateRange, Long excludeBookingId) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                propertyId,
                BookingStatus.CONFIRMED,
                dateRange.getStartDate(),
                dateRange.getEndDate(),
                excludeBookingId
        );

        if (!overlappingBookings.isEmpty()) {
            List<Long> overlappingIds = overlappingBookings.stream()
                    .map(Booking::getId)
                    .toList();
            throw new BookingOverlapException("The property is already booked for the selected dates", overlappingIds);
        }
    }

    private void validateNoOverlappingBlocks(long propertyId, DateRange dateRange) {
        List<Block> overlappingBlocks = blockRepository.findOverlapping(
                propertyId,
                dateRange.getStartDate(),
                dateRange.getEndDate()
        );

        if (!overlappingBlocks.isEmpty()) {
            List<Long> overlappingIds = overlappingBlocks.stream()
                    .map(Block::getId)
                    .toList();
            throw new BookingBlockException("The property is blocked for the selected dates", overlappingIds);
        }
    }
} 