package mate.academy.spring.online.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

@SpringBootApplication
=======
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
>>>>>>> backup-9059aa8
public class SpringOnlineBookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOnlineBookstoreApplication.class, args);
    }
}
