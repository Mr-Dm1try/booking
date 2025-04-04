package inc.pomoika.booking.manage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inc.pomoika.booking.common.model.Block;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("""
        SELECT b FROM Block b 
        WHERE b.propertyId = :propertyId 
            AND (:excludeBlockId IS NULL OR b.id != :excludeBlockId) 
            AND b.startDate <= :endDate AND b.endDate >= :startDate
    """)
    List<Block> findOverlapping(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeBlockId") Long excludeBlockId
    );
} 