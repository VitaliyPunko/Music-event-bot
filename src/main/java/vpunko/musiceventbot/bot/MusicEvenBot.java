package vpunko.musiceventbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vpunko.musiceventbot.bot.handler.MusicEventCoreHandler;
import vpunko.musiceventbot.bot.handler.TelegramBotMessageBuilder;

import java.util.List;

@Service
public class MusicEvenBot extends TelegramLongPollingBot {

    public static final String FIND_YOUR_FAVORITE_ARTISTS_EVENT = "Find your favorite artist's event";
    public static final String FIND_AN_EVENT_IN_YOUR_CITY = "Find an event in your city";

    private final String botUsername;
    private final String botToken;
    private final TelegramBotMessageBuilder handler;
    private final MusicEventCoreHandler coreHandler;
    private final UserStateManager userStateManager;

    public MusicEvenBot(
            @Value("${telegram.bot_username}") String botUsername,
            @Value("${telegram.bot_token}") String botToken,
            TelegramBotMessageBuilder handler,
            MusicEventCoreHandler coreHandler,
            UserStateManager userStateManager) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.handler = handler;
        this.coreHandler = coreHandler;
        this.userStateManager = userStateManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            BotUserState userState = userStateManager.getUserState(chatId);

            if (messageText.equals("/start")) {
                message.setText("Please, login via your telegram account");
                sendMessage(message);

                message.setText("Click the button below to log in:");
                var loginButton = handler.loginButton();
                var markup = new InlineKeyboardMarkup();
                markup.setKeyboard(List.of(List.of(loginButton)));

                message.setReplyMarkup(markup);
                sendMessage(message);

                userStateManager.setUserState(chatId, BotUserState.START);
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

                userStateManager.setUserState(chatId, BotUserState.AUTHENTICATED);

            } else if (messageText.equals(FIND_YOUR_FAVORITE_ARTISTS_EVENT)) {
                message.setText("Please, enter the name of the artist or band");
                sendMessage(message);
                userStateManager.setUserState(chatId, BotUserState.WAITING_FOR_ARTIST_NAME);

            } else if (messageText.equals(FIND_AN_EVENT_IN_YOUR_CITY)) {

            } else if (userState == BotUserState.WAITING_FOR_ARTIST_NAME) {
                //logic to call music-event-service
                List<String> eventByArtist = coreHandler.getMusicEventByArtist(messageText);
                sendEventsToUser(chatId, eventByArtist);

                message.setText("Please, enter /continue to choose another options");
                sendMessage(message);
            }
        }
    }

    public void sendEventsToUser(long chatId, List<String> events) {
        for (String event : events) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(event);
            sendMessage.setParseMode("HTML"); // Enable HTML formatting

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.getStackTrace();
            }
        }
    }


    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.getStackTrace();
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
