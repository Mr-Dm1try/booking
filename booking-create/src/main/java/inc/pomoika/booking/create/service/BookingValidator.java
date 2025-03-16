package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.exception.BookingBlockException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public void validateBooking(Booking booking) {
        List<Booking> overlappingBookings = bookingRepository.findOverlapping(
                booking.getPropertyId(),
                booking.getStartDate(),
                booking.getEndDate(),
                BookingStatus.CONFIRMED
        );
        
        if (!overlappingBookings.isEmpty()) {
            throw new BookingOverlapException(
                "The property is already booked for the selected dates", 
                overlappingBookings.stream().map(Booking::getId).toList()
            );
        }

        // There could be call to another service to check if the property is blocked
        List<Block> overlappingBlocks = blockRepository.findOverlapping(
                booking.getPropertyId(),
                booking.getStartDate(),
                booking.getEndDate()
        );
        
        if (!overlappingBlocks.isEmpty()) {
            throw new BookingBlockException(
                "The property is blocked for the selected dates", 
                overlappingBlocks.stream().map(Block::getId).toList()
            );
        }
    }
} 