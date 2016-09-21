package hu.farago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.common.eventbus.EventBus;

@SpringBootApplication
@EnableAsync
public class TrdrApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrdrApplication.class, args);
	}
	
	@Bean
	public EventBus eventBus() {
		return new EventBus();
	}
}
