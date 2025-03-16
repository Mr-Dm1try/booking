package inc.pomoika.booking.manage.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.manage.repository.BookingRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    private static final long PROPERTY_ID = 1L;
    private static final long GUEST_ID = 1L;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
    }

    @Test
    void deleteBooking_Success() throws Exception {
        // given
        Booking existingBooking = new Booking()
            .setPropertyId(PROPERTY_ID)
            .setGuestId(GUEST_ID)
            .setStartDate(LocalDate.of(2024, 1, 1))
            .setEndDate(LocalDate.of(2024, 1, 10))
            .setStatus(BookingStatus.CONFIRMED);
        existingBooking = bookingRepository.save(existingBooking);

        // when/then
        mockMvc.perform(delete("/api/v1/bookings/{id}", existingBooking.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBooking_WhenNotFound_ReturnsNotFound() throws Exception {
        // when/then
        mockMvc.perform(delete("/api/v1/bookings/{id}", 999L))
                .andExpect(status().isNotFound());
    }
} 