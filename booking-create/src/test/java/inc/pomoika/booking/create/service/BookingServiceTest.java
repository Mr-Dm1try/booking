package inc.pomoika.booking.create.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inc.pomoika.booking.common.exception.BookingBlockException;
import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingResponse;
import inc.pomoika.booking.create.model.dto.BookingUpdateRequest;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BookingValidator bookingValidator;

    @InjectMocks
    private BookingService bookingService;

    private static final long PROPERTY_ID = 1L;
    private static final long GUEST_ID = 2L;
    private static final long BOOKING_ID = 1L;

    @Test
    void createBooking_Success() {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        DateRange dateRange = new DateRange(startDate, endDate);

        BookingCreationRequest request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(dateRange)
                .build();

        Booking savedBooking = new Booking()
                .setId(BOOKING_ID)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any())).thenReturn(savedBooking);

        // When
        BookingResponse response = bookingService.createBooking(request);

        // Then
        assertNotNull(response);
        assertEquals(BOOKING_ID, response.getId());
        assertEquals(PROPERTY_ID, response.getPropertyId());
        assertEquals(GUEST_ID, response.getGuestId());
        assertEquals(startDate, response.getDateRange().getStartDate());
        assertEquals(endDate, response.getDateRange().getEndDate());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());

        verify(bookingValidator).validateBooking(eq(PROPERTY_ID), eq(dateRange));
        verify(bookingRepository).save(any());
    }

    @Test
    void updateBooking_Success() {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        DateRange newDateRange = new DateRange(newStartDate, newEndDate);

        Booking existingBooking = new Booking()
                .setId(BOOKING_ID)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(newDateRange)
                .build();

        Booking updatedBooking = new Booking()
                .setId(BOOKING_ID)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(newStartDate)
                .setEndDate(newEndDate)
                .setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any())).thenReturn(updatedBooking);

        // When
        BookingResponse response = bookingService.updateBooking(BOOKING_ID, request);

        // Then
        assertNotNull(response);
        assertEquals(BOOKING_ID, response.getId());
        assertEquals(PROPERTY_ID, response.getPropertyId());
        assertEquals(GUEST_ID, response.getGuestId());
        assertEquals(newStartDate, response.getDateRange().getStartDate());
        assertEquals(newEndDate, response.getDateRange().getEndDate());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());

        verify(bookingValidator).validateBookingUpdate(eq(PROPERTY_ID), eq(newDateRange), eq(BOOKING_ID));
        verify(bookingRepository).findById(BOOKING_ID);
        verify(bookingRepository).save(any());
    }

    @Test
    void updateBooking_WhenBookingNotFound_ThrowsException() {
        // Given
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        DateRange newDateRange = new DateRange(newStartDate, newEndDate);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(newDateRange)
                .build();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(BOOKING_ID, request));

        verify(bookingRepository).findById(BOOKING_ID);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(bookingValidator);
    }

    @Test
    void updateBooking_WhenOverlappingBooking_ThrowsException() {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        DateRange newDateRange = new DateRange(newStartDate, newEndDate);

        Booking existingBooking = new Booking()
                .setId(BOOKING_ID)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(newDateRange)
                .build();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(existingBooking));
        doThrow(new BookingOverlapException("The property is already booked for the selected dates", List.of(2L)))
                .when(bookingValidator).validateBookingUpdate(eq(PROPERTY_ID), eq(newDateRange), eq(BOOKING_ID));

        // When/Then
        assertThrows(BookingOverlapException.class,
                () -> bookingService.updateBooking(BOOKING_ID, request));

        verify(bookingRepository).findById(BOOKING_ID);
        verify(bookingValidator).validateBookingUpdate(eq(PROPERTY_ID), eq(newDateRange), eq(BOOKING_ID));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void updateBooking_WhenBlockExists_ThrowsException() {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        DateRange newDateRange = new DateRange(newStartDate, newEndDate);

        Booking existingBooking = new Booking()
                .setId(BOOKING_ID)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(newDateRange)
                .build();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(existingBooking));
        doThrow(new BookingBlockException("The property is blocked for the selected dates", List.of(1L)))
                .when(bookingValidator).validateBookingUpdate(eq(PROPERTY_ID), eq(newDateRange), eq(BOOKING_ID));

        // When/Then
        assertThrows(BookingBlockException.class,
                () -> bookingService.updateBooking(BOOKING_ID, request));

        verify(bookingRepository).findById(BOOKING_ID);
        verify(bookingValidator).validateBookingUpdate(eq(PROPERTY_ID), eq(newDateRange), eq(BOOKING_ID));
        verifyNoMoreInteractions(bookingRepository);
    }
} 