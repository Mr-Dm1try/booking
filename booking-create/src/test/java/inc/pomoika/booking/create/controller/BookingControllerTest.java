package inc.pomoika.booking.create.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BlockRepository blockRepository;

    private static final long PROPERTY_ID = 1L;
    private static final long GUEST_ID = 2L;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        blockRepository.deleteAll();
    }

    @Test
    void createBooking_Success() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        BookingCreationRequest request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.guestId").value(GUEST_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.dateRange.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.name()));
    }

    @Test
    void createBooking_WhenOverlappingBooking_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        // Create an existing booking
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(3L)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        existingBooking = bookingRepository.save(existingBooking);

        BookingCreationRequest request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The property is already booked for the selected dates"))
                .andExpect(jsonPath("$.code").value("BOOKING_OVERLAP"))
                .andExpect(jsonPath("$.params.ids[0]").value(existingBooking.getId()));
    }

    @Test
    void createBooking_WhenBlockExists_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        // Create a block
        Block block = new Block()
                .setPropertyId(PROPERTY_ID)
                .setStartDate(startDate)
                .setEndDate(endDate);
        block = blockRepository.save(block);

        BookingCreationRequest request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The property is blocked for the selected dates"))
                .andExpect(jsonPath("$.code").value("BLOCK_OVERLAP"))
                .andExpect(jsonPath("$.params.ids[0]").value(block.getId()));
    }

    @Test
    void createBooking_WhenInvalidDateRange_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.minusDays(1);
        
        BookingCreationRequest request = BookingCreationRequest.builder()
                .propertyId(PROPERTY_ID)
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End date must be after start date"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
} 