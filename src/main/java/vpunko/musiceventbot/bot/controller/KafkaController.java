package vpunko.musiceventbot.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vpunko.musiceventbot.bot.dto.UserMessageRequestEvent;
import vpunko.musiceventbot.bot.publisher.UserMessageRequestBotEventKafkaPublisher;

@Slf4j
@RestController
public class KafkaController {

    @Autowired
    private UserMessageRequestBotEventKafkaPublisher userMessageRequestBotEventKafkaPublisher;

    @GetMapping("/send/{message}")
    public String sendMessage(@PathVariable String message) {
        log.info("Sending message via KafkaController : {}", message);
        long chatId = 1L;
        UserMessageRequestEvent user = new UserMessageRequestEvent(chatId, message, true);
        userMessageRequestBotEventKafkaPublisher.sendMessage(user, chatId);
        return message;
    }
}
