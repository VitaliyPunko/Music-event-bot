package vpunko.musiceventbot.listener;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import vpunko.musiceventbot.bot.MusicEventBot;
import vpunko.musiceventbot.bot.dto.TicketMasterResponseEvent;
import vpunko.musiceventbot.bot.dto.TicketmasterEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = "ticket-master-response-topic-test",
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@TestPropertySource(properties = {
        "spring.kafka.in.ticket-master-response-event.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
class TicketmasterEventKafkaListenerIT {

    private static final String TOPIC = "ticket-master-response-topic-test";

    @Autowired
    private KafkaTemplate<String, TicketMasterResponseEvent> kafkaTemplate;

    @MockitoBean
    private MusicEventBot musicEventBot;

    @Test
    void shouldConsumeEventAndCallService() {
        // given
        TicketMasterResponseEvent event = createTicketMasterResponseEvent();
        SendMessage message = new SendMessage();
        message.setChatId(event.getChatId());

        // when
        kafkaTemplate.send(TOPIC, event);

        // then
        verify(musicEventBot, timeout(5000))
                .handleKafkaArtistNameInput(
                        any(SendMessage.class),
                        eq(event.getChatId()),
                        eq(event.getEvents())
                );
    }

    private TicketMasterResponseEvent createTicketMasterResponseEvent() {
        TicketmasterEvent event1 = new TicketmasterEvent();
        event1.setName("Test group1");
        event1.setPrice(15.15);
        TicketmasterEvent event2 = new TicketmasterEvent();
        event2.setName("Test group2");
        event2.setPrice(30.1);

        List<TicketmasterEvent> events = List.of(event1, event2);

        TicketMasterResponseEvent event = new TicketMasterResponseEvent();
        event.setEvents(events);
        event.setChatId(123456789L);
        return event;
    }

    @TestConfiguration
    static class KafkaTestConfig {

        @Value("${spring.embedded.kafka.brokers}")
        private String bootstrapServers;

        @Bean
        public ProducerFactory<String, TicketMasterResponseEvent> producerFactoryTest() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        public KafkaTemplate<String, TicketMasterResponseEvent> kafkaTemplateTest(ProducerFactory<String, TicketMasterResponseEvent> producerFactoryTest) {
            return new KafkaTemplate<>(producerFactoryTest);
        }
    }
}