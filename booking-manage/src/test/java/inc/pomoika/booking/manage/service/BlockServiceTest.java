package inc.pomoika.booking.manage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.model.dto.DateRange;
import inc.pomoika.booking.common.service.PropertyLockService;
import inc.pomoika.booking.manage.model.dto.BlockRequest;
import inc.pomoika.booking.manage.model.dto.BlockResponse;
import inc.pomoika.booking.manage.repository.BlockRepository;
import inc.pomoika.booking.manage.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {
    @Mock
    private BlockRepository blockRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyLockService propertyLockService;

    @InjectMocks
    private BlockService blockService;

    private static final long PROPERTY_ID = 1L;
    private static final long BLOCK_ID = 1L;
    private static final DateRange DATE_RANGE = new DateRange(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 10)
    );

    @Test
    void createBlock_Success() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Block block = new Block()
                .setId(BLOCK_ID)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(blockRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(blockRepository.save(any())).thenReturn(block);

        // when
        BlockResponse result = blockService.createBlock(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(BLOCK_ID);
        assertThat(result.getPropertyId()).isEqualTo(PROPERTY_ID);
        assertThat(result.getDateRange()).isEqualTo(DATE_RANGE);
        verify(propertyLockService).createLock(PROPERTY_ID);
        verify(propertyLockService).acquireLock(PROPERTY_ID);
        verify(blockRepository).save(any());
    }

    @Test
    void createBlock_WhenOverlappingBooking_ThrowsException() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Booking overlappingBooking = new Booking()
                .setId(1L)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate())
                .setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(overlappingBooking));

        // when/then
        assertThatThrownBy(() -> blockService.createBlock(request))
                .isInstanceOf(BookingOverlapException.class)
                .hasMessage("Cannot block dates that overlap with existing bookings");
    }

    @Test
    void createBlock_WhenOverlappingBlock_ThrowsException() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Block overlappingBlock = new Block()
                .setId(1L)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(blockRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(overlappingBlock));

        // when/then
        assertThatThrownBy(() -> blockService.createBlock(request))
                .isInstanceOf(BookingOverlapException.class)
                .hasMessage("Cannot create block that overlaps with existing blocks");
    }

    @Test
    void updateBlock_Success() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Block existingBlock = new Block()
                .setId(BLOCK_ID)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(blockRepository.findById(BLOCK_ID)).thenReturn(Optional.of(existingBlock));
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(blockRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(blockRepository.save(any())).thenReturn(existingBlock);

        // when
        BlockResponse result = blockService.updateBlock(BLOCK_ID, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(BLOCK_ID);
        assertThat(result.getPropertyId()).isEqualTo(PROPERTY_ID);
        assertThat(result.getDateRange()).isEqualTo(DATE_RANGE);
        verify(propertyLockService).createLock(PROPERTY_ID);
        verify(propertyLockService).acquireLock(PROPERTY_ID);
        verify(blockRepository).save(any());
    }

    @Test
    void updateBlock_WhenOverlappingBooking_ThrowsException() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Block existingBlock = new Block()
                .setId(BLOCK_ID)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(blockRepository.findById(BLOCK_ID)).thenReturn(Optional.of(existingBlock));
        Booking overlappingBooking = new Booking()
                .setId(1L)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate())
                .setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(overlappingBooking));

        // when/then
        assertThatThrownBy(() -> blockService.updateBlock(BLOCK_ID, request))
                .isInstanceOf(BookingOverlapException.class)
                .hasMessage("Cannot block dates that overlap with existing bookings");
    }

    @Test
    void updateBlock_WhenOverlappingBlock_ThrowsException() {
        // given
        BlockRequest request = BlockRequest.builder()
                .propertyId(PROPERTY_ID)
                .dateRange(DATE_RANGE)
                .build();
        Block existingBlock = new Block()
                .setId(BLOCK_ID)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(blockRepository.findById(BLOCK_ID)).thenReturn(Optional.of(existingBlock));
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        Block overlappingBlock = new Block()
                .setId(2L)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(blockRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(overlappingBlock));

        // when/then
        assertThatThrownBy(() -> blockService.updateBlock(BLOCK_ID, request))
                .isInstanceOf(BookingOverlapException.class)
                .hasMessage("Cannot create block that overlaps with existing blocks");
    }

    @Test
    void deleteBlock_Success() {
        // given
        Block existingBlock = new Block()
                .setId(BLOCK_ID)
                .setPropertyId(PROPERTY_ID)
                .setStartDate(DATE_RANGE.getStartDate())
                .setEndDate(DATE_RANGE.getEndDate());
        when(blockRepository.findById(BLOCK_ID)).thenReturn(Optional.of(existingBlock));

        // when
        blockService.deleteBlock(BLOCK_ID);

        // then
        verify(propertyLockService).createLock(PROPERTY_ID);
        verify(propertyLockService).acquireLock(PROPERTY_ID);
        verify(blockRepository).delete(existingBlock);
    }
} 