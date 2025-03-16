package inc.pomoika.booking.create.repository;

import inc.pomoika.booking.common.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    
    @Query("SELECT b FROM Block b WHERE " +
           "b.propertyId = :propertyId AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Block> findOverlapping(
            @Param("propertyId") long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
} 