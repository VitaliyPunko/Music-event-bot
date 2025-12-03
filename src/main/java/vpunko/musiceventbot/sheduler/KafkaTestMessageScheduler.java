package vpunko.musiceventbot.sheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vpunko.musiceventbot.bot.dto.UserMessageRequestEvent;
import vpunko.musiceventbot.bot.publisher.UserMessageRequestBotEventKafkaPublisher;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTestMessageScheduler {

    private static final List<String> ARTISTS = List.of(
            "Adele", "Rammstein", "Taylor Swift", "Ed Sheeran", "Imagine Dragons",
            "Metallica", "Coldplay", "Billie Eilish", "Queen", "The Weeknd",
            "Linkin Park", "Drake", "Kendrick Lamar", "Bruno Mars", "Lady Gaga",
            "BTS", "Katy Perry", "Eminem", "Ariana Grande", "Depeche Mode",
            "Red Hot Chili Peppers", "Madonna", "U2", "Nirvana", "Shakira",
            "Lana Del Rey", "Post Malone", "Selena Gomez", "Doja Cat", "The Rolling Stones",
            "Pink Floyd", "David Guetta", "Muse", "Green Day", "Hans Zimmer",
            "John Williams", "Sia", "Jennifer Lopez", "Sam Smith", "Harry Styles",
            "Justin Bieber", "ABBA", "Gorillaz", "The Killers", "Oasis",
            "The Beatles", "Black Eyed Peas", "Twenty One Pilots", "Kings of Leon"
    );

    private final Random random = new Random();
    private final UserMessageRequestBotEventKafkaPublisher userMessageRequestBotEventKafkaPublisher;

    // runs every 30 seconds, for example
    // @Scheduled(fixedRateString = "30000")
    public void sendTestMessage() {
        long chatId = 1L;
        String artist = ARTISTS.get(random.nextInt(ARTISTS.size()));
        log.info("Scheduler sending artist to Kafka: {}", artist);

        UserMessageRequestEvent user =
                new UserMessageRequestEvent(chatId, artist, true);

        userMessageRequestBotEventKafkaPublisher.sendMessage(user, chatId);
    }
}
