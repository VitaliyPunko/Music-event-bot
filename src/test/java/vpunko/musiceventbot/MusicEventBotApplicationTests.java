package vpunko.musiceventbot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@ActiveProfiles("test")
@SpringBootTest
class MusicEventBotApplicationTests {

    @Test
    void contextLoads() {
    }

    @Bean
    public TelegramBotsApi telegramBotsApiTestStub() {
        return null;
    }
}
