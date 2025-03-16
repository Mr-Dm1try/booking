package inc.pomoika.booking.manage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.manage.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    @Transactional
    public void deleteBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        
        bookingRepository.delete(booking);
    }
}