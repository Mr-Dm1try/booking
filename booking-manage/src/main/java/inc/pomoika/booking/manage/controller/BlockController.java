package inc.pomoika.booking.manage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inc.pomoika.booking.manage.model.dto.BlockRequest;
import inc.pomoika.booking.manage.model.dto.BlockResponse;
import inc.pomoika.booking.manage.service.BlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockResponse> createBlock(@Valid @RequestBody BlockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(blockService.createBlock(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlockResponse> updateBlock(
        @PathVariable("id") long blockId, @Valid @RequestBody BlockRequest request
    ) {
        return ResponseEntity.ok(blockService.updateBlock(blockId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable("id") long blockId) {
        blockService.deleteBlock(blockId);
        return ResponseEntity.noContent().build();
    }
} 