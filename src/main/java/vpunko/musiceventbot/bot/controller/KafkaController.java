package vpunko.musiceventbot.bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vpunko.musiceventbot.bot.dto.User;
import vpunko.musiceventbot.bot.publisher.MusicCoreKafkaPublisher;

@RestController
public class KafkaController {

    @Autowired
    private MusicCoreKafkaPublisher musicCoreKafkaPublisher;

    @GetMapping("/send/{message}")
    public String sendMessage(@PathVariable String message) {
        User user = new User(message);
        musicCoreKafkaPublisher.sendMessage(user);
        return message;
    }
}
