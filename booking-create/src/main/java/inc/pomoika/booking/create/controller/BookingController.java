package inc.pomoika.booking.create.controller;

import inc.pomoika.booking.create.service.BookingService;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingResponse;
import inc.pomoika.booking.create.model.dto.BookingUpdateRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }
    
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable long bookingId,
            @Valid @RequestBody BookingUpdateRequest request
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, request));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable long bookingId
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @PutMapping("/{bookingId}/rebook")
    public ResponseEntity<BookingResponse> rebookCancelledBooking(
            @PathVariable long bookingId,
            @Valid @RequestBody BookingUpdateRequest request
    ) {
        return ResponseEntity.ok(bookingService.rebookCancelledBooking(bookingId, request));
    }
} 