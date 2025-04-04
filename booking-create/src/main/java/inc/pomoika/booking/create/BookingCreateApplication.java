package inc.pomoika.booking.create;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"inc.pomoika.booking"})
@EntityScan(basePackages = {"inc.pomoika.booking.common.model"})
@EnableJpaRepositories(basePackages = {
    "inc.pomoika.booking.create.repository",
    "inc.pomoika.booking.common.repository"
})
public class BookingCreateApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingCreateApplication.class, args);
    }
} 