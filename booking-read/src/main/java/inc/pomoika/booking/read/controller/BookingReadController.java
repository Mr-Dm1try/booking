package inc.pomoika.booking.read.controller;

import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.read.model.dto.BookingReadResponse;
import inc.pomoika.booking.read.service.BookingReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingReadController {

    private final BookingReadService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<BookingReadResponse> getBookingById(@PathVariable long id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<BookingReadResponse>> getBookingsByPropertyId(@PathVariable long propertyId) {
        return ResponseEntity.ok(bookingService.findByPropertyId(propertyId));
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<BookingReadResponse>> getBookingsByGuestId(@PathVariable long guestId) {
        return ResponseEntity.ok(bookingService.findByGuestId(guestId));
    }

    @GetMapping("/overlapping")
    public ResponseEntity<List<BookingReadResponse>> getOverlappingBookings(
            @RequestParam long propertyId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "CONFIRMED") BookingStatus status
    ) {
        return ResponseEntity.ok(bookingService.findOverlappingBookings(propertyId, startDate, endDate, status));
    }

    @GetMapping("/property/{propertyId}/status/{status}")
    public ResponseEntity<List<BookingReadResponse>> getBookingsByPropertyIdAndStatus(
            @PathVariable long propertyId,
            @PathVariable BookingStatus status
    ) {
        return ResponseEntity.ok(bookingService.findByPropertyIdAndStatus(propertyId, status));
    }

    @GetMapping("/guest/{guestId}/status/{status}")
    public ResponseEntity<List<BookingReadResponse>> getBookingsByGuestIdAndStatus(
            @PathVariable long guestId,
            @PathVariable BookingStatus status
    ) {
        return ResponseEntity.ok(bookingService.findByGuestIdAndStatus(guestId, status));
    }
}