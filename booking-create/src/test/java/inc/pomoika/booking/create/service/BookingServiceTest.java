package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.exception.BookingBlockException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingResponse;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private BookingCreationRequest request;
    private LocalDate startDate;
    private LocalDate endDate;
    private static final long PROPERTY_ID = 1L;
    private static final long GUEST_ID = 2L;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(3);
        request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();
    }

    @Test
    void createBooking_Success() {
        // Given
        Booking savedBooking = new Booking()
                .setId(1L)
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        doNothing().when(bookingValidator).validateBooking(any(Booking.class));

        // When
        BookingResponse response = bookingService.createBooking(request);

        // Then
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getId()).isEqualTo(savedBooking.getId());
                    assertThat(r.getPropertyId()).isEqualTo(PROPERTY_ID);
                    assertThat(r.getGuestId()).isEqualTo(GUEST_ID);
                    assertThat(r.getDateRange().getStartDate()).isEqualTo(startDate);
                    assertThat(r.getDateRange().getEndDate()).isEqualTo(endDate);
                    assertThat(r.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
                });

        verify(bookingValidator).validateBooking(any(Booking.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenOverlappingBookingExists_ThrowsException() {
        // Given
        doThrow(new BookingOverlapException("The property is already booked for the selected dates", List.of(3L)))
            .when(bookingValidator).validateBooking(any(Booking.class));

        // When/Then
        assertThatThrownBy(() -> bookingService.createBooking(request))
            .isInstanceOf(BookingOverlapException.class)
            .satisfies(e -> {
                BookingOverlapException ex = (BookingOverlapException) e;
                assertThat(ex.getBookingIds()).containsExactly(3L);
            });

        verify(bookingValidator).validateBooking(any(Booking.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenBlockExists_ThrowsException() {
        // Given
        doThrow(new BookingBlockException("The property is blocked for the selected dates", List.of(4L)))
            .when(bookingValidator).validateBooking(any(Booking.class));

        // When/Then
        assertThatThrownBy(() -> bookingService.createBooking(request))
            .isInstanceOf(BookingBlockException.class)
            .satisfies(e -> {
                BookingBlockException ex = (BookingBlockException) e;
                assertThat(ex.getBlockIds()).containsExactly(4L);
            });

        verify(bookingValidator).validateBooking(any(Booking.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenInvalidDateRange_ThrowsException() {
        // Given
        LocalDate invalidEndDate = startDate.minusDays(1);
        BookingCreationRequest invalidRequest = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, invalidEndDate))
                .build();

        doThrow(new IllegalArgumentException("End date must be after start date"))
            .when(bookingValidator).validateBooking(any(Booking.class));

        // When/Then
        assertThatThrownBy(() -> bookingService.createBooking(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("End date must be after start date");

        verify(bookingValidator).validateBooking(any(Booking.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
} 