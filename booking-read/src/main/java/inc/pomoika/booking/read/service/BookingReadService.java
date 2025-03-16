package inc.pomoika.booking.read.service;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.read.model.dto.BookingReadResponse;
import inc.pomoika.booking.read.repository.BookingReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingReadService {
    
    private final BookingReadRepository bookingRepository;

    private BookingReadResponse toResponse(Booking booking) {
        return BookingReadResponse.builder()
                .id(booking.getId())
                .propertyId(booking.getPropertyId())
                .guestId(booking.getGuestId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
    
    public Optional<BookingReadResponse> findById(long id) {
        return bookingRepository.findById(id)
                .map(this::toResponse);
    }
    
    public List<BookingReadResponse> findByPropertyId(long propertyId) {
        return bookingRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public List<BookingReadResponse> findByGuestId(long guestId) {
        return bookingRepository.findByGuestId(guestId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public List<BookingReadResponse> findOverlappingBookings(long propertyId, LocalDate startDate, LocalDate endDate, BookingStatus status) {
        return bookingRepository.findOverlappingBookings(propertyId, startDate, endDate, status)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public List<BookingReadResponse> findByPropertyIdAndStatus(long propertyId, BookingStatus status) {
        return bookingRepository.findByPropertyIdAndStatus(propertyId, status)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public List<BookingReadResponse> findByGuestIdAndStatus(long guestId, BookingStatus status) {
        return bookingRepository.findByGuestIdAndStatus(guestId, status)
                .stream()
                .map(this::toResponse)
                .toList();
    }
} 