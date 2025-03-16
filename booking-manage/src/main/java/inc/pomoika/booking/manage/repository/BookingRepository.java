package inc.pomoika.booking.manage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.propertyId = :propertyId 
            AND b.status = :status 
            AND b.startDate <= :endDate AND b.endDate >= :startDate
    """)
    List<Booking> findOverlapping(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") BookingStatus status
    );
} 