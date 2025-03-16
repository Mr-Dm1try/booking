package inc.pomoika.booking.create.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.exception.IllegalBookingStatusException;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.service.PropertyLockService;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingResponse;
import inc.pomoika.booking.create.model.dto.BookingUpdateRequest;
import inc.pomoika.booking.create.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final PropertyLockService propertyLockService;

    @Transactional
    public BookingResponse createBooking(BookingCreationRequest request) {
        acquireLock(request.getPropertyId());

        bookingValidator.validateBooking(request.getPropertyId(), request.getDateRange());

        Booking booking = toEntity(request);
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse updateBooking(long bookingId, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalBookingStatusException("Cannot update cancelled booking", bookingId, booking.getStatus().name());
        }

        acquireLock(booking.getPropertyId());

        bookingValidator.validateBookingUpdate(booking.getPropertyId(), request.getDateRange(), bookingId);

        updateEntity(booking, request);
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse rebookCancelledBooking(long bookingId, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalBookingStatusException("Only cancelled bookings can be rebooked", bookingId, booking.getStatus().name());
        }

        acquireLock(booking.getPropertyId());
        
        bookingValidator.validateBooking(booking.getPropertyId(), request.getDateRange());

        booking.setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate())
                .setStatus(BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    private void acquireLock(long propertyId) {
        // Create lock in a separate transaction
        propertyLockService.createLock(propertyId);
        // Try to acquire the lock in the main transaction
        propertyLockService.acquireLock(propertyId);
    }

    private static Booking toEntity(BookingCreationRequest request) {
        return new Booking()
                .setPropertyId(request.getPropertyId())
                .setGuestId(request.getGuestId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate())
                .setStatus(BookingStatus.CONFIRMED);
    }

    private static void updateEntity(Booking booking, BookingUpdateRequest request) {
        booking.setGuestId(request.getGuestId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate());
    }

    private static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPropertyId(),
                booking.getGuestId(),
                new DateRange(booking.getStartDate(), booking.getEndDate()),
                booking.getStatus()
        );
    }
} 