package vpunko.musiceventbot.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import vpunko.musiceventbot.bot.MusicEventBot;

/**
 * REST Controller для приёма webhook от Telegram.
 * Telegram отправляет POST запросы на /webhook с Update объектом.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final MusicEventBot bot;

    @PostMapping(value = "/webhook")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        log.info("📨 Webhook received - UpdateId: {}, HasMessage: {}, HasCallback: {}",
                update.getUpdateId(),
                update.hasMessage(),
                update.hasCallbackQuery());

        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("💬 Message text: {}", update.getMessage().getText());
        }

        try {
            BotApiMethod<?> response = bot.onWebhookUpdateReceived(update);

            if (response != null) {
                log.info("✅ Response prepared, sending back to Telegram");
                return ResponseEntity.ok(response);
            } else {
                log.info("✅ Update processed successfully, no response needed");
                return ResponseEntity.ok().build();
            }

        } catch (Exception e) {
            log.error("❌ ERROR processing webhook: {}", e.getMessage(), e);
            // Telegram ждёт 200 OK даже при ошибке, чтобы не ретраить
            return ResponseEntity.ok().build();
        }
    }
}
