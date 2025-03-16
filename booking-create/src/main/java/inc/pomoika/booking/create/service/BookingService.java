package inc.pomoika.booking.create.service;

import inc.pomoika.booking.create.repository.BookingRepository;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;

    @Transactional
    public BookingCreationResponse createBooking(BookingCreationRequest request) {
        Booking booking = toEntity(request);
        bookingValidator.validateBooking(booking);
        Booking savedBooking = bookingRepository.save(booking);
        return toResponse(savedBooking);
    }

    private static Booking toEntity(BookingCreationRequest request) {
        return new Booking()
                .setPropertyId(request.getPropertyId())
                .setGuestId(request.getGuestId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate())
                .setStatus(BookingStatus.CONFIRMED);
    }

    private static BookingCreationResponse toResponse(Booking booking) {
        return BookingCreationResponse.builder()
                .id(booking.getId())
                .propertyId(booking.getPropertyId())
                .guestId(booking.getGuestId())
                .dateRange(new DateRange(booking.getStartDate(), booking.getEndDate()))
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
} 