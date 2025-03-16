package inc.pomoika.booking.create.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import inc.pomoika.booking.common.exception.BookingBlockException;
import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation error", e);
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request content");
        return ResponseEntity.badRequest()
                .body(ErrorResponse.validationError(message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Validation error", e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.validationError(e.getMessage()));
    }

    @ExceptionHandler(BookingOverlapException.class)
    public ResponseEntity<ErrorResponse> handleBookingOverlapException(BookingOverlapException e) {
        log.warn("Booking overlap with bookings [{}] detected", e.getBookingIds(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.ofBookings(e.getMessage(), e.getBookingIds()));
    }

    @ExceptionHandler(BookingBlockException.class)
    public ResponseEntity<ErrorResponse> handleBookingBlockException(BookingBlockException e) {
        log.warn("Booking overlap with blocks [{}] detected", e.getBlockIds(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.ofBlocks(e.getMessage(), e.getBlockIds()));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException e) {
        log.warn("Booking not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.notFound(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of("An unexpected error occurred"));
    }
} 