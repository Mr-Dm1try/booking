package inc.pomoika.booking.read.service;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.read.model.dto.BookingReadResponse;
import inc.pomoika.booking.read.repository.BookingReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingReadServiceTest {

    @Mock
    private BookingReadRepository bookingRepository;

    @InjectMocks
    private BookingReadService bookingService;

    private Booking createSampleBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPropertyId(100L);
        booking.setGuestId(200L);
        booking.setStartDate(LocalDate.of(2024, 3, 1));
        booking.setEndDate(LocalDate.of(2024, 3, 5));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return booking;
    }

    @Test
    void findById_WhenBookingExists_ReturnsBookingResponse() {
        // Arrange
        Booking booking = createSampleBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        Optional<BookingReadResponse> result = bookingService.findById(1L);

        // Assert
        assertThat(result).isPresent();
        BookingReadResponse response = result.get();
        assertThat(response.getId()).isEqualTo(booking.getId());
        assertThat(response.getPropertyId()).isEqualTo(booking.getPropertyId());
        assertThat(response.getGuestId()).isEqualTo(booking.getGuestId());
        assertThat(response.getStartDate()).isEqualTo(booking.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(booking.getEndDate());
        assertThat(response.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void findById_WhenBookingDoesNotExist_ReturnsEmpty() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<BookingReadResponse> result = bookingService.findById(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByPropertyId_ReturnsListOfBookings() {
        // Arrange
        Booking booking = createSampleBooking();
        when(bookingRepository.findByPropertyId(100L)).thenReturn(List.of(booking));

        // Act
        List<BookingReadResponse> result = bookingService.findByPropertyId(100L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPropertyId()).isEqualTo(100L);
    }

    @Test
    void findByGuestId_ReturnsListOfBookings() {
        // Arrange
        Booking booking = createSampleBooking();
        when(bookingRepository.findByGuestId(200L)).thenReturn(List.of(booking));

        // Act
        List<BookingReadResponse> result = bookingService.findByGuestId(200L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGuestId()).isEqualTo(200L);
    }

    @Test
    void findOverlappingBookings_ReturnsListOfBookings() {
        // Arrange
        Booking booking = createSampleBooking();
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 5);
        when(bookingRepository.findOverlappingBookings(100L, startDate, endDate, BookingStatus.CONFIRMED))
                .thenReturn(List.of(booking));

        // Act
        List<BookingReadResponse> result = bookingService.findOverlappingBookings(100L, startDate, endDate, BookingStatus.CONFIRMED);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDate()).isEqualTo(startDate);
        assertThat(result.get(0).getEndDate()).isEqualTo(endDate);
    }

    @Test
    void findByPropertyIdAndStatus_ReturnsListOfBookings() {
        // Arrange
        Booking booking = createSampleBooking();
        when(bookingRepository.findByPropertyIdAndStatus(100L, BookingStatus.CONFIRMED))
                .thenReturn(List.of(booking));

        // Act
        List<BookingReadResponse> result = bookingService.findByPropertyIdAndStatus(100L, BookingStatus.CONFIRMED);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPropertyId()).isEqualTo(100L);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void findByGuestIdAndStatus_ReturnsListOfBookings() {
        // Arrange
        Booking booking = createSampleBooking();
        when(bookingRepository.findByGuestIdAndStatus(200L, BookingStatus.CONFIRMED))
                .thenReturn(List.of(booking));

        // Act
        List<BookingReadResponse> result = bookingService.findByGuestIdAndStatus(200L, BookingStatus.CONFIRMED);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGuestId()).isEqualTo(200L);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
} 