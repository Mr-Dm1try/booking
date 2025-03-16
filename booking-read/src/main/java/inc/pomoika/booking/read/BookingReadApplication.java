package inc.pomoika.booking.read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "inc.pomoika.booking.read",
    "inc.pomoika.booking.common"
})
@EntityScan(basePackages = {
    "inc.pomoika.booking.common.model"
})
@EnableJpaRepositories(basePackages = {
    "inc.pomoika.booking.read.repository",
    "inc.pomoika.booking.common.repository"
})
public class BookingReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingReadApplication.class, args);
    }
} 