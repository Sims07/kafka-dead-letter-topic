package sc.kafka.deadlettertopic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfiguration {

  @Bean
  public SeekToCurrentErrorHandler errorHandler(
      DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
    return new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(1, 1));
  }

  @Bean
  public DeadLetterPublishingRecoverer publisher(KafkaOperations<String, String> bytesTemplate) {
    return new DeadLetterPublishingRecoverer(bytesTemplate);
  }
}
