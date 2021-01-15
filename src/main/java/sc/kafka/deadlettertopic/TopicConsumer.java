package sc.kafka.deadlettertopic;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TopicConsumer {

  public static final String HELLO_WORLD_TOPIC = "topicWithDLT";

  private final HelloWorldService helloWorldService;

  public TopicConsumer(HelloWorldService helloWorldService) {
    this.helloWorldService = helloWorldService;
  }

  @KafkaListener(topics = HELLO_WORLD_TOPIC)
  public void consume(String message) {
    helloWorldService.hello(message);
  }
}
