package inc.pomoika.booking.manage.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.manage.model.dto.BlockRequest;
import inc.pomoika.booking.manage.model.dto.BlockResponse;
import inc.pomoika.booking.manage.repository.BlockRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlockRepository blockRepository;

    private static final long PROPERTY_ID = 1L;
    private static final DateRange DATE_RANGE = new DateRange(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 10)
    );

    @BeforeEach
    void setUp() {
        blockRepository.deleteAll();
    }

    @Test
    void createBlock_Success() throws Exception {
        // given
        BlockRequest request = new BlockRequest(PROPERTY_ID, DATE_RANGE);

        // when/then
        mockMvc.perform(post("/api/v1/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.dateRange.endDate").value("2024-01-10"));
    }

    @Test
    void createBlock_WhenOverlapping_ReturnsBadRequest() throws Exception {
        // given
        Block existingBlock = new Block()
            .setPropertyId(PROPERTY_ID)
            .setStartDate(DATE_RANGE.getStartDate())
            .setEndDate(DATE_RANGE.getEndDate());
        existingBlock = blockRepository.save(existingBlock);

        BlockRequest request = new BlockRequest(PROPERTY_ID, DATE_RANGE);

        // when/then
        mockMvc.perform(post("/api/v1/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBlock_Success() throws Exception {
        // given
        Block existingBlock = new Block()
            .setPropertyId(PROPERTY_ID)
            .setStartDate(DATE_RANGE.getStartDate())
            .setEndDate(DATE_RANGE.getEndDate());
        existingBlock = blockRepository.save(existingBlock);

        BlockRequest request = new BlockRequest(PROPERTY_ID, DATE_RANGE);

        // when/then
        mockMvc.perform(put("/api/v1/blocks/{id}", existingBlock.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID))
                .andExpect(jsonPath("$.dateRange.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.dateRange.endDate").value("2024-01-10"));
    }

    @Test
    void updateBlock_WhenOverlapping_ReturnsBadRequest() throws Exception {
        // given
        Block existingBlock = new Block()
            .setPropertyId(PROPERTY_ID)
            .setStartDate(DATE_RANGE.getStartDate())
            .setEndDate(DATE_RANGE.getEndDate());
        existingBlock = blockRepository.save(existingBlock);

        Block overlappingBlock = new Block()
            .setPropertyId(PROPERTY_ID)
            .setStartDate(LocalDate.of(2024, 1, 5))
            .setEndDate(LocalDate.of(2024, 1, 15));
        blockRepository.save(overlappingBlock);

        BlockRequest request = new BlockRequest(PROPERTY_ID, DATE_RANGE);

        // when/then
        mockMvc.perform(put("/api/v1/blocks/{id}", existingBlock.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBlock_Success() throws Exception {
        // given
        Block existingBlock = new Block()
            .setPropertyId(PROPERTY_ID)
            .setStartDate(DATE_RANGE.getStartDate())
            .setEndDate(DATE_RANGE.getEndDate());
        existingBlock = blockRepository.save(existingBlock);

        // when/then
        mockMvc.perform(delete("/api/v1/blocks/{id}", existingBlock.getId()))
                .andExpect(status().isNoContent());
    }
} 