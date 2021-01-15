package sc.kafka.deadlettertopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TopicConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(TopicConsumer.class);

  @KafkaListener(topicPattern = "topicWithDLT")
  public void consume(String message) {
    LOGGER.info(message);
  }
}
