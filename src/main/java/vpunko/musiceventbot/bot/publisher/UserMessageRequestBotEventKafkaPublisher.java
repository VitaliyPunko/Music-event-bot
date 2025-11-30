package vpunko.musiceventbot.bot.publisher;

import io.micrometer.core.annotation.Counted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vpunko.musiceventbot.bot.dto.UserMessageRequestEvent;

import static vpunko.musiceventbot.bot.constant.MetricsConstant.USER_MESSAGE_REQUEST_PUBLISHER_COUNTER;

/**
 * Producer for sending a user message to music-core service via Kafka
 */
@Slf4j
@Component
public class UserMessageRequestBotEventKafkaPublisher {

    private final String topicName;
    private final KafkaTemplate<String, UserMessageRequestEvent> kafkaTemplate;

    public UserMessageRequestBotEventKafkaPublisher(
            KafkaTemplate<String, UserMessageRequestEvent> kafkaTemplate,
            @Value("${spring.kafka.out.music-core-event.topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Counted(value = USER_MESSAGE_REQUEST_PUBLISHER_COUNTER)
    public void sendMessage(UserMessageRequestEvent message, Long chatId) {
        log.info("Sending message to music-core service : {}", message);
        kafkaTemplate.send(topicName, String.valueOf(chatId), message);
    }
}