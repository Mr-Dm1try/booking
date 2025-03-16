package inc.pomoika.booking.create.repository;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.status = :status AND b.propertyId = :propertyId 
            AND (:excludeBookingId IS NULL OR b.id != :excludeBookingId) 
            AND ((b.startDate <= :endDate AND b.endDate >= :startDate))
    """)
    List<Booking> findOverlappingBookings(
            @Param("propertyId") long propertyId,
            @Param("status") BookingStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeBookingId") Long excludeBookingId
    );
} 