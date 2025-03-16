package inc.pomoika.booking.create.config;

import inc.pomoika.booking.create.repository.BlockRepository;
import inc.pomoika.booking.create.repository.BookingRepository;
import inc.pomoika.booking.create.repository.PropertyLockRepository;
import inc.pomoika.booking.create.service.BookingService;
import inc.pomoika.booking.create.service.BookingValidator;
import inc.pomoika.booking.create.service.PropertyLockService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@TestConfiguration
@EnableJpaRepositories(basePackages = "inc.pomoika.booking.create.repository")
public class IntegrationTestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public PropertyLockService propertyLockService(PropertyLockRepository propertyLockRepository) {
        return new PropertyLockService(propertyLockRepository);
    }

    @Bean
    public BookingValidator bookingValidator(BookingRepository bookingRepository, BlockRepository blockRepository) {
        return new BookingValidator(bookingRepository, blockRepository);
    }

    @Bean
    public BookingService bookingService(BookingRepository bookingRepository, BookingValidator bookingValidator, PropertyLockService propertyLockService) {
        return new BookingService(bookingRepository, bookingValidator, propertyLockService);
    }
} 