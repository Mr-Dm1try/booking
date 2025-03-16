package inc.pomoika.booking.manage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inc.pomoika.booking.common.exception.BookingNotFoundException;
import inc.pomoika.booking.common.exception.BookingOverlapException;
import inc.pomoika.booking.common.model.Block;
import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import inc.pomoika.booking.common.service.PropertyLockService;
import inc.pomoika.booking.manage.model.dto.BlockRequest;
import inc.pomoika.booking.manage.model.dto.BlockResponse;
import inc.pomoika.booking.manage.repository.BlockRepository;
import inc.pomoika.booking.manage.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final BookingRepository bookingRepository;
    private final PropertyLockService propertyLockService;

    @Transactional
    public BlockResponse createBlock(BlockRequest request) {
        acquireLock(request.getPropertyId());
        
        checkForOverlappingBookings(request);
        checkForOverlappingBlocks(request);

        Block block = new Block()
                .setPropertyId(request.getPropertyId())
                .setStartDate(request.getDateRange().getStartDate())
                .setEndDate(request.getDateRange().getEndDate());

        return BlockResponse.of(blockRepository.save(block));
    }

    @Transactional
    public BlockResponse updateBlock(long blockId, BlockRequest request) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new BookingNotFoundException("Block not found"));

        acquireLock(request.getPropertyId());

        checkForOverlappingBookings(request);
        checkForOverlappingBlocks(request, blockId);

        block.setStartDate(request.getDateRange().getStartDate())
            .setEndDate(request.getDateRange().getEndDate());

        return BlockResponse.of(blockRepository.save(block));
    }

    @Transactional
    public void deleteBlock(long blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new BookingNotFoundException("Block not found"));
        
        acquireLock(block.getPropertyId());
        blockRepository.delete(block);
    }

    private void acquireLock(long propertyId) {
        // Create lock in a separate transaction
        propertyLockService.createLock(propertyId);
        // Try to acquire the lock in the main transaction
        propertyLockService.acquireLock(propertyId);
    }

    private void checkForOverlappingBookings(BlockRequest request) {
        List<Booking> overlappingBookings = bookingRepository.findOverlapping(
                request.getPropertyId(),
                request.getDateRange().getStartDate(),
                request.getDateRange().getEndDate(),
                BookingStatus.CONFIRMED
        );

        if (!overlappingBookings.isEmpty()) {
            throw new BookingOverlapException("Cannot block dates that overlap with existing bookings",
                    overlappingBookings.stream().map(Booking::getId).toList());
        }
    }

    private void checkForOverlappingBlocks(BlockRequest request) {
        checkForOverlappingBlocks(request, null);
    }

    private void checkForOverlappingBlocks(BlockRequest request, Long excludeBlockId) {
        List<Block> overlappingBlocks = blockRepository.findOverlapping(
                request.getPropertyId(),
                request.getDateRange().getStartDate(),
                request.getDateRange().getEndDate(),
                excludeBlockId
        );

        if (!overlappingBlocks.isEmpty()) {
            throw new BookingOverlapException("Cannot create block that overlaps with existing blocks",
                    overlappingBlocks.stream().map(Block::getId).toList());
        }
    }
} 