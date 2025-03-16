package inc.pomoika.booking.common.repository;

import inc.pomoika.booking.common.model.PropertyLock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyLockRepository extends JpaRepository<PropertyLock, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM PropertyLock l WHERE l.propertyId = :propertyId")
    PropertyLock findByPropertyIdForUpdate(@Param("propertyId") long propertyId);
}  