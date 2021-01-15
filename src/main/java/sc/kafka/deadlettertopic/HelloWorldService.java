package sc.kafka.deadlettertopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldService.class);

  public void hello(String message) {
    LOGGER.info("Hello world {}", message);
  }
}
