package vpunko.musiceventbot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Profile("test")
@Configuration
@TestConfiguration
public class Config {

    @Bean
    public TelegramBotsApi telegramBotsApiTestStub() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
