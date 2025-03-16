package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingResponse;
import inc.pomoika.booking.create.model.dto.BookingUpdateRequest;
import inc.pomoika.booking.create.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;

    @Transactional
    public BookingResponse createBooking(BookingCreationRequest request) {
        bookingValidator.validateBooking(request.getPropertyId(), request.getDateRange());
        Booking booking = toEntity(request);
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        bookingValidator.validateBookingUpdate(booking.getPropertyId(), request.getDateRange(), bookingId);
        updateEntity(booking, request);
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    private Booking toEntity(BookingCreationRequest request) {
        return new Booking()
                .setPropertyId(request.getPropertyId())
                .setGuestId(request.getGuestId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate())
                .setStatus(BookingStatus.CONFIRMED);
    }

    private void updateEntity(Booking booking, BookingUpdateRequest request) {
        booking.setGuestId(request.getGuestId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate());
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPropertyId(),
                booking.getGuestId(),
                new DateRange(booking.getStartDate(), booking.getEndDate()),
                booking.getStatus()
        );
    }
} 