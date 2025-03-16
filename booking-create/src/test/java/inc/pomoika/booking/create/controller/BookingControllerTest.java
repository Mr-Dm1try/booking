package inc.pomoika.booking.create.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.create.model.dto.BookingCreationRequest;
import inc.pomoika.booking.create.model.dto.BookingUpdateRequest;
import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;

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
    private static final long NEW_GUEST_ID = 3L;

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
    void updateBooking_Success() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        existingBooking = bookingRepository.save(existingBooking);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(NEW_GUEST_ID)
                .dateRange(new DateRange(newStartDate, newEndDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}", existingBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingBooking.getId()))
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.guestId").value(NEW_GUEST_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value(newStartDate.toString()))
                .andExpect(jsonPath("$.dateRange.endDate").value(newEndDate.toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.name()));
    }

    @Test
    void updateBooking_WhenOverlappingBooking_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate overlappingStartDate = LocalDate.now().plusDays(2);
        LocalDate overlappingEndDate = LocalDate.now().plusDays(4);
        
        // Create booking to update
        Booking bookingToUpdate = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        bookingToUpdate = bookingRepository.save(bookingToUpdate);

        // Create overlapping booking
        Booking overlappingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(NEW_GUEST_ID)
                .setStartDate(overlappingStartDate)
                .setEndDate(overlappingEndDate)
                .setStatus(BookingStatus.CONFIRMED);
        overlappingBooking = bookingRepository.save(overlappingBooking);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(NEW_GUEST_ID)
                .dateRange(new DateRange(overlappingStartDate, overlappingEndDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}", bookingToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The property is already booked for the selected dates"))
                .andExpect(jsonPath("$.code").value("BOOKING_OVERLAP"))
                .andExpect(jsonPath("$.params.ids[0]").value(overlappingBooking.getId()));
    }

    @Test
    void updateBooking_WhenBlockExists_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate blockedStartDate = LocalDate.now().plusDays(4);
        LocalDate blockedEndDate = LocalDate.now().plusDays(6);
        
        // Create booking to update
        Booking bookingToUpdate = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        bookingToUpdate = bookingRepository.save(bookingToUpdate);

        // Create block
        Block block = new Block()
                .setPropertyId(PROPERTY_ID)
                .setStartDate(blockedStartDate)
                .setEndDate(blockedEndDate);
        block = blockRepository.save(block);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(NEW_GUEST_ID)
                .dateRange(new DateRange(blockedStartDate, blockedEndDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}", bookingToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The property is blocked for the selected dates"))
                .andExpect(jsonPath("$.code").value("BLOCK_OVERLAP"))
                .andExpect(jsonPath("$.params.ids[0]").value(block.getId()));
    }

    @Test
    void updateBooking_WhenInvalidDateRange_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate invalidStartDate = LocalDate.now().plusDays(4);
        LocalDate invalidEndDate = LocalDate.now().plusDays(2); // End date before start date
        
        // Create booking to update
        Booking bookingToUpdate = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        bookingToUpdate = bookingRepository.save(bookingToUpdate);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(NEW_GUEST_ID)
                .dateRange(new DateRange(invalidStartDate, invalidEndDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}", bookingToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End date must be after start date"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createBooking_WhenOverlappingBooking_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        // Create an existing booking
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(NEW_GUEST_ID)
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
        LocalDate endDate = startDate.minusDays(1); // End date before start date
        
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

    @Test
    void cancelBooking_Success() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        existingBooking = bookingRepository.save(existingBooking);

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}/cancel", existingBooking.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingBooking.getId()))
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.guestId").value(GUEST_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.dateRange.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CANCELLED.name()));
    }

    @Test
    void cancelBooking_WhenAlreadyCancelled_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CANCELLED);
        existingBooking = bookingRepository.save(existingBooking);

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}/cancel", existingBooking.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingBooking.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CANCELLED.name()));
    }

    @Test
    void rebookCancelledBooking_Success() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CANCELLED);
        existingBooking = bookingRepository.save(existingBooking);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}/rebook", existingBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingBooking.getId()))
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.guestId").value(GUEST_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.dateRange.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.name()));
    }

    @Test
    void rebookCancelledBooking_WhenNotCancelled_ReturnsBadRequest() throws Exception {
        // Given
        Booking existingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(LocalDate.now().plusDays(1))
                .setEndDate(LocalDate.now().plusDays(3))
                .setStatus(BookingStatus.CONFIRMED);
        existingBooking = bookingRepository.save(existingBooking);

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}/rebook", existingBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BookingUpdateRequest.builder()
                        .guestId(GUEST_ID)
                        .dateRange(DateRange.builder()
                                .startDate(LocalDate.now().plusDays(4))
                                .endDate(LocalDate.now().plusDays(6))
                                .build())
                        .build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only cancelled bookings can be rebooked"));
    }

    @Test
    void rebookCancelledBooking_WhenOverlappingBooking_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        // Create cancelled booking
        Booking cancelledBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CANCELLED);
        cancelledBooking = bookingRepository.save(cancelledBooking);

        // Create overlapping booking
        Booking overlappingBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(NEW_GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CONFIRMED);
        overlappingBooking = bookingRepository.save(overlappingBooking);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(GUEST_ID)
                .dateRange(new DateRange(startDate, endDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}/rebook", cancelledBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The property is already booked for the selected dates"))
                .andExpect(jsonPath("$.code").value("BOOKING_OVERLAP"))
                .andExpect(jsonPath("$.params.ids[0]").value(overlappingBooking.getId()));
    }

    @Test
    void updateBooking_WhenBookingIsCancelled_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        LocalDate newStartDate = LocalDate.now().plusDays(4);
        LocalDate newEndDate = LocalDate.now().plusDays(6);
        
        Booking cancelledBooking = new Booking()
                .setPropertyId(PROPERTY_ID)
                .setGuestId(GUEST_ID)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(BookingStatus.CANCELLED);
        cancelledBooking = bookingRepository.save(cancelledBooking);

        BookingUpdateRequest request = BookingUpdateRequest.builder()
                .guestId(NEW_GUEST_ID)
                .dateRange(new DateRange(newStartDate, newEndDate))
                .build();

        // When/Then
        mockMvc.perform(put("/api/v1/bookings/{id}", cancelledBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot update cancelled booking"))
                .andExpect(jsonPath("$.code").value("ILLEGAL_BOOKING_STATUS"))
                .andExpect(jsonPath("$.params.id").value(cancelledBooking.getId()));
    }
} 