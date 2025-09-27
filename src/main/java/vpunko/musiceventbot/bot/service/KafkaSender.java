package vpunko.musiceventbot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vpunko.musiceventbot.bot.dto.User;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, User> kafkaTemplate;

    public void sendMessage(User message, String topicName) {
        log.info("Sending : {}", message);
        log.info("--------------------------------");

        kafkaTemplate.send(topicName, message);
    }
}