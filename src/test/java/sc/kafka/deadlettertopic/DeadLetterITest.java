package sc.kafka.deadlettertopic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka
public class DeadLetterITest {

  @Mock private Appender mockedAppender;
  @Captor private ArgumentCaptor<LoggingEvent> loggingEventCaptor;
  @Autowired KafkaTemplate<String, String> kafkaTemplate;

  @BeforeEach
  public void setup() {
    Logger root = (Logger) LoggerFactory.getLogger(TopicConsumer.class);
    root.addAppender(mockedAppender);
    root.setLevel(Level.INFO);
  }

  @Test
  public void failed_consumer_should_send_to_DLT() throws InterruptedException {
    kafkaTemplate.send("topicWithDLT", "Test");

    Thread.sleep(1000);

    BDDMockito.then(mockedAppender).should().doAppend(loggingEventCaptor.capture());
    BDDAssertions.then(loggingEventCaptor.getValue()).isNotNull();
  }
}
