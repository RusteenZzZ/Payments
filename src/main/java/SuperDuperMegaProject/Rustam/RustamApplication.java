package SuperDuperMegaProject.Rustam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RustamApplication {

	public static void main(String[] args) {
		SpringApplication.run(RustamApplication.class, args);
	}

}
