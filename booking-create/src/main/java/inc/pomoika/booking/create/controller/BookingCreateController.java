package inc.pomoika.booking.create.controller;

import inc.pomoika.booking.create.service.BookingService;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingCreateController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingCreationResponse> createBooking(@Valid @RequestBody BookingCreationRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }
} 