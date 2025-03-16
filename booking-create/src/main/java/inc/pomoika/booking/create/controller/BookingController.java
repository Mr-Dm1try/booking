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
    
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable("id") long id,
            @Valid @RequestBody BookingUpdateRequest request
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable("id") long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/{id}/rebook")
    public ResponseEntity<BookingResponse> rebookCancelledBooking(@PathVariable("id") long id) {
        return ResponseEntity.ok(bookingService.rebookCancelledBooking(id));
    }
} 