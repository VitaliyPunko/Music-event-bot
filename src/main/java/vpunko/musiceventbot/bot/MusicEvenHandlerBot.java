package vpunko.musiceventbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vpunko.musiceventbot.bot.handler.TelegramBotMessageBuilder;

import java.util.List;

@Service
public class MusicEvenHandlerBot extends TelegramLongPollingBot {

    public static final String FIND_YOUR_FAVORITE_ARTISTS_EVENT = "Find your favorite artist's event";
    public static final String FIND_AN_EVENT_IN_YOUR_CITY = "Find an event in your city";
    private final String botUsername;
    private final String botToken;
    private final TelegramBotMessageBuilder handler;

    public MusicEvenHandlerBot(
            @Value("${telegram.bot_username}") String botUsername,
            @Value("${telegram.bot_token}") String botToken,
            TelegramBotMessageBuilder handler) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.handler = handler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);

            if (messageText.equals("/start")) {
                message.setText("Please, login via your telegram account");
                sendMessage(message);

                message.setText("Click the button below to log in:");
                var loginButton = handler.loginButton();
                var markup = new InlineKeyboardMarkup();
                markup.setKeyboard(List.of(List.of(loginButton)));

                message.setReplyMarkup(markup);
                sendMessage(message);

            } else if (messageText.equals("/next")) {
                // User was successfully authenticated
                message.setText("✅ Authentication successful! Now you can get events.");

                // Add two buttons for the next option
                ReplyKeyboardMarkup keyboardMarkup = handler.getReplyKeyboardMarkup(
                        FIND_YOUR_FAVORITE_ARTISTS_EVENT,
                        FIND_AN_EVENT_IN_YOUR_CITY
                );
                message.setReplyMarkup(keyboardMarkup);

                sendMessage(message);

            } else if (messageText.equals(FIND_YOUR_FAVORITE_ARTISTS_EVENT)) {
                message.setText("Please, enter the name of the artist or band");
                sendMessage(message);
                //logic to call music-event-service

            } else if (messageText.equals(FIND_AN_EVENT_IN_YOUR_CITY)) {

            }



        }
    }



    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


}
