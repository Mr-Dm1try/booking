package inc.pomoika.booking.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import inc.pomoika.booking.common.model.PropertyLock;
import inc.pomoika.booking.common.repository.PropertyLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyLockService {
    private final PropertyLockRepository propertyLockRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void acquireLock(long propertyId) {
        // Acquire lock in the current transaction
        propertyLockRepository.findByPropertyIdForUpdate(propertyId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLock(long propertyId) {
        PropertyLock lock = new PropertyLock(propertyId);
        propertyLockRepository.save(lock);
    }
}