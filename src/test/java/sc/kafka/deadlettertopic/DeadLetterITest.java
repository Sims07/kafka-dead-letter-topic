package sc.kafka.deadlettertopic;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.timeout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import java.time.Duration;
import java.util.Collections;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Headers;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = TopicConsumer.HELLO_WORLD_TOPIC)
class DeadLetterITest {

  @SpyBean private HelloWorldService helloWorldService;
  @Mock private Appender<ILoggingEvent> mockedAppender;
  @Captor private ArgumentCaptor<LoggingEvent> loggingEventCaptor;

  @Autowired KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private DefaultKafkaConsumerFactory<String, String> defaultKafkaConsumerFactory;

  @BeforeEach
  public void setup() {
    Logger root = (Logger) LoggerFactory.getLogger(HelloWorldService.class);
    root.addAppender(mockedAppender);
    root.setLevel(Level.INFO);
  }

  @Test
  void consumer_should_consume_message_log_hello_world_message() {
    kafkaTemplate.send(TopicConsumer.HELLO_WORLD_TOPIC, "Test");

    BDDMockito.then(mockedAppender).should(timeout(1000)).doAppend(loggingEventCaptor.capture());
    thenMessageLoggedIsEqualsTO("Hello world Test");
  }

  @Test
  void given_consumer_exception_then_should_send_in_DLT() throws InterruptedException {
    willThrow(new RuntimeException()).given(helloWorldService).hello(anyString());

    kafkaTemplate.send(TopicConsumer.HELLO_WORLD_TOPIC, "Test");

    thenExpectedNbMessagesInTopic(
        TopicConsumer.HELLO_WORLD_TOPIC + ".DLT",
        1,
        consumerRecord -> {
          Headers headers = consumerRecord.headers();
          then(consumerRecord.value()).isEqualTo("Test");
          then(headers).isNotEmpty();
          then(headers.headers("kafka_dlt-exception-message")).isNotEmpty();
        });
  }

  private void thenExpectedNbMessagesInTopic(
      String targetedTopic,
      int nbExpectedMessages,
      java.util.function.Consumer<? super ConsumerRecord<String, String>> thenMessageConsumer) {
    Consumer<String, String> testConsumerDLT =
        defaultKafkaConsumerFactory.createConsumer("testConsumer", "DLT", "Listener");
    testConsumerDLT.subscribe(Collections.singletonList(targetedTopic));
    ConsumerRecords<String, String> consumerRecords = testConsumerDLT.poll(Duration.ofSeconds(3));
    then(consumerRecords.count()).isEqualTo(nbExpectedMessages);
    consumerRecords.records(targetedTopic).forEach(thenMessageConsumer);
  }

  private void thenMessageLoggedIsEqualsTO(String expectedLogMessage) {
    LoggingEvent eventLog = loggingEventCaptor.getValue();
    BDDAssertions.then(eventLog).isNotNull();
    BDDAssertions.then(eventLog.getFormattedMessage()).isEqualTo(expectedLogMessage);
  }
}
