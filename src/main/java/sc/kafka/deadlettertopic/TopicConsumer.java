package sc.kafka.deadlettertopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TopicConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TopicConsumer.class);
  public static final String HELLO_WORLD_TOPIC = "topicWithDLT";
  private final HelloWorldService helloWorldService;

  public TopicConsumer(HelloWorldService helloWorldService) {
    this.helloWorldService = helloWorldService;
  }

  @KafkaListener(topics = HELLO_WORLD_TOPIC)
  public void consume(String message) {
    try {
      helloWorldService.hello(message);
    } catch (Exception e) {
      LOGGER.error("err", e);
      throw e;
    }
  }
}
