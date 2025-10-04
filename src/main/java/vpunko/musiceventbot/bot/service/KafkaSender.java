package vpunko.musiceventbot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vpunko.musiceventbot.bot.dto.User;


@Slf4j
@Component
public class KafkaSender {

    private final String topicName;
    private final KafkaTemplate<String, User> kafkaTemplate;

    public KafkaSender(KafkaTemplate<String, User> kafkaTemplate,
                       @Value("${spring.kafka.out.telegram-bot-user-received-event.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendMessage(User message) {
        log.info("Sending : {}", message);
        log.info("--------------------------------");

        kafkaTemplate.send(topicName, message);
    }
}