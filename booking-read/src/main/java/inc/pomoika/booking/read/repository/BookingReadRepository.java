package inc.pomoika.booking.read.repository;

import inc.pomoika.booking.common.model.Booking;
import inc.pomoika.booking.common.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingReadRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByPropertyId(long propertyId);
    
    List<Booking> findByGuestId(long guestId);
    
    @Query("SELECT b FROM Booking b WHERE b.propertyId = :propertyId " +
           "AND b.status = :status " +
           "AND ((b.startDate BETWEEN :startDate AND :endDate) " +
           "OR (b.endDate BETWEEN :startDate AND :endDate) " +
           "OR (:startDate BETWEEN b.startDate AND b.endDate))")
    List<Booking> findOverlappingBookings(
            @Param("propertyId") long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") BookingStatus status
    );
    
    List<Booking> findByPropertyIdAndStatus(long propertyId, BookingStatus status);
    
    List<Booking> findByGuestIdAndStatus(long guestId, BookingStatus status);
} 