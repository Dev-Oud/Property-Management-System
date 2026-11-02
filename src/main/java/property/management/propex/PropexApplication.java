package property.management.propex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PropexApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropexApplication.class, args);
	}

}
