package inc.pomoika.booking.read.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.read.repository.BookingReadRepository;

@SpringBootTest
@AutoConfigureMockMvc
class BookingReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingReadRepository bookingRepository;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        testBooking = createAndSaveTestBooking();
    }

    private Booking createAndSaveTestBooking() {
        Booking booking = new Booking();
        booking.setPropertyId(100L);
        booking.setGuestId(200L);
        booking.setStartDate(LocalDate.of(2024, 3, 1));
        booking.setEndDate(LocalDate.of(2024, 3, 5));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Test
    void getBookingById_WhenExists_ReturnsBooking() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/{id}", testBooking.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
        assertThat(content).contains("100");  // propertyId
        assertThat(content).contains("200");  // guestId
    }

    @Test
    void getBookingById_WhenNotExists_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByPropertyId_ReturnsBookings() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/property/{propertyId}", 100)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
        assertThat(content).contains("100");  // propertyId
    }

    @Test
    void getBookingsByGuestId_ReturnsBookings() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/guest/{guestId}", 200)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
        assertThat(content).contains("200");  // guestId
    }

    @Test
    void getOverlappingBookings_ReturnsBookings() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/overlapping")
                        .param("propertyId", "100")
                        .param("startDate", "2024-03-01")
                        .param("endDate", "2024-03-05")
                        .param("status", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
    }

    @Test
    void getBookingsByPropertyIdAndStatus_ReturnsBookings() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/property/{propertyId}/status/{status}", 100, "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
        assertThat(content).contains("CONFIRMED");
    }

    @Test
    void getBookingsByGuestIdAndStatus_ReturnsBookings() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/bookings/guest/{guestId}/status/{status}", 200, "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(String.valueOf(testBooking.getId()));
        assertThat(content).contains("CONFIRMED");
    }
} 