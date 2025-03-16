package inc.pomoika.booking.create.service;

import inc.pomoika.booking.common.model.PropertyLock;
import inc.pomoika.booking.create.repository.PropertyLockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestTransactionConfiguration.class)
class PropertyLockServiceTest {

    @Autowired
    private PropertyLockService propertyLockService;

    @Autowired
    private PropertyLockRepository propertyLockRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void createLock_WhenLockDoesNotExist_ShouldCreateIt() {
        // given
        long propertyId = 1L;

        // when
        propertyLockService.createLock(propertyId);

        // then
        PropertyLock lock = propertyLockRepository.findById(propertyId).orElse(null);
        assertThat(lock).isNotNull();
        assertThat(lock.getPropertyId()).isEqualTo(propertyId);
    }

    @Test
    void createLock_WhenLockExists_ShouldNotThrowException() {
        // given
        long propertyId = 1L;
        propertyLockService.createLock(propertyId);

        // when & then - should not throw
        propertyLockService.createLock(propertyId);
    }

    @Test
    @Transactional
    void acquireLock_WhenLockExists_ShouldAcquireIt() {
        // given
        long propertyId = 1L;
        propertyLockService.createLock(propertyId);

        // when & then - should not throw
        propertyLockService.acquireLock(propertyId);
    }

    @Test
    void concurrentLockAcquisition_ShouldWaitForFirstTransactionToComplete() throws Exception {
        // given
        long propertyId = 1L;
        propertyLockService.createLock(propertyId);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // when
            CompletableFuture<Duration> firstThread = CompletableFuture.supplyAsync(() -> {
                return measureExecutionTime(() -> {
                    transactionTemplate.execute(status -> {
                        propertyLockService.acquireLock(propertyId);
                        sleep(1000); // Hold the lock for 1 second
                        return null;
                    });
                });
            }, executor);

            // Let the first thread acquire the lock
            sleep(100);

            CompletableFuture<Duration> secondThread = CompletableFuture.supplyAsync(() -> {
                return measureExecutionTime(() -> {
                    transactionTemplate.execute(status -> {
                        propertyLockService.acquireLock(propertyId);
                        return null;
                    });
                });
            }, executor);

            Duration firstDuration = firstThread.get();
            Duration secondDuration = secondThread.get();

            // then
            assertThat(firstDuration).isGreaterThanOrEqualTo(Duration.ofMillis(1000));
            assertThat(secondDuration).isGreaterThanOrEqualTo(Duration.ofMillis(800)); // More lenient timing check
        } finally {
            executor.shutdown();
        }
    }

    private Duration measureExecutionTime(Runnable action) {
        Instant start = Instant.now();
        action.run();
        return Duration.between(start, Instant.now());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
} 