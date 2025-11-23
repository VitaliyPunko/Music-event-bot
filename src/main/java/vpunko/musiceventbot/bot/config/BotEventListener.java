package vpunko.musiceventbot.bot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import vpunko.musiceventbot.bot.MusicEventBot;
import vpunko.musiceventbot.bot.dto.BotConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotEventListener {

    private final MusicEventBot bot;
    private final BotConfig botConfig;

    private boolean initialized = false;

    /**
     * Инициализация бота ПОСЛЕ полного запуска Spring Context.
     * <p>
     * ContextRefreshedEvent публикуется когда:
     * - Все beans созданы и инициализированы
     * - База данных подключена
     * - Веб-сервер запущен
     * - Actuator endpoints готовы
     * <p>
     * Это гарантирует, что регистрация webhook произойдёт
     * когда приложение готово принимать HTTP запросы от Telegram.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        if (initialized) {
            log.debug("Bot already initialized, skipping");
            return;
        }

        log.info("Initializing Telegram bot...");
        log.info("Webhook URL: {}", botConfig.getWebhookUrl());

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            SetWebhook setWebhook = SetWebhook.builder()
                    .url(botConfig.getWebhookUrl())
                    .maxConnections(40)
                    .dropPendingUpdates(true)  // Игнорировать старые сообщения
                    .build();

            // Регистрируем бот с webhook
            telegramBotsApi.registerBot(bot, setWebhook);

            initialized = true;
            log.info("✅ Telegram bot registered successfully!");
            log.info("Bot username: {}", bot.getBotUsername());
            log.info("Ready to receive updates via webhook");

        } catch (TelegramApiException e) {
            log.error("❌ Failed to register Telegram bot", e);
            log.error("Webhook URL: {}", botConfig.getWebhookUrl());
            log.error("Please check:");
            log.error(" If Bot token is valid");
            log.error(" If Webhook URL is accessible from internet");
            log.error(" If SSL certificate is valid");

            throw new RuntimeException("Bot initialization failed", e);
        }
    }
}
