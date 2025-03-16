package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.model.PropertyLock;
import inc.pomoika.booking.create.repository.PropertyLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropertyLockService {
    private final PropertyLockRepository propertyLockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLock(long propertyId) {
        try {
            PropertyLock lock = new PropertyLock(propertyId);
            propertyLockRepository.saveAndFlush(lock);
        } catch (DataIntegrityViolationException ignore) {
            // Row already exists, which is fine
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void acquireLock(long propertyId) {
        propertyLockRepository.findByPropertyIdForUpdate(propertyId);
    }
} 