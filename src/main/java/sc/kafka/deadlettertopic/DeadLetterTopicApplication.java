package sc.kafka.deadlettertopic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeadLetterTopicApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeadLetterTopicApplication.class, args);
	}

}
