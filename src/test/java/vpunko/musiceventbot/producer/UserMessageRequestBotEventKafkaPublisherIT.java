package vpunko.musiceventbot.producer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import vpunko.musiceventbot.TestKafkaConsumersUtils;
import vpunko.musiceventbot.bot.dto.UserMessageRequestEvent;
import vpunko.musiceventbot.bot.publisher.UserMessageRequestBotEventKafkaPublisher;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = "music-event-core-topic"
)
@TestPropertySource(properties = {
        "spring.kafka.out.music-core-event.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
class UserMessageRequestBotEventKafkaPublisherIT {

    private static final String TOPIC = "music-event-core-topic";

    @Autowired
    private UserMessageRequestBotEventKafkaPublisher publisher;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void shouldPublishEventToKafkaTopic() {
        // given
        Consumer<String, UserMessageRequestEvent> consumer =
                TestKafkaConsumersUtils.create(
                        embeddedKafka,
                        TOPIC,
                        UserMessageRequestEvent.class
                );

        long chatId = 123456L;
        UserMessageRequestEvent event = new UserMessageRequestEvent(chatId, "test message");

        // when
        publisher.sendMessage(event, chatId);

        // then
        ConsumerRecord<String, UserMessageRequestEvent> record =
                KafkaTestUtils.getSingleRecord(consumer, TOPIC, Duration.ofSeconds(5));

        assertThat(record.key()).isEqualTo(String.valueOf(chatId));
        assertThat(record.value())
                .usingRecursiveComparison()
                .isEqualTo(event);
    }

}
