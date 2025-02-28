package vpunko.musiceventbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vpunko.musiceventbot.bot.handler.UserInputHandler;

import java.util.Collections;

@Service
public class MusicEvenHandlerBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final UserInputHandler handler;

    public MusicEvenHandlerBot(
            @Value("${telegram.bot_username}") String botUsername,
            @Value("${telegram.bot_token}") String botToken,
            UserInputHandler handler) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.handler = handler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            if ("/login".equals(messageText)) {

                // Inline-кнопка для авторизации
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Click the button below to log in:");

                InlineKeyboardButton loginButton = new InlineKeyboardButton();
                loginButton.setText("Login with Telegram");
                //for local test run ngrok and change url here and in telegram.html. And set this url as domain in Telegram
                loginButton.setUrl("https://dceb-138-199-47-215.ngrok-free.app/auth/telegram");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(Collections.singletonList(Collections.singletonList(loginButton)));

                message.setReplyMarkup(markup);
                sendMessage(message);
            }
            else if (update.hasMessage() && update.getMessage().getText().startsWith("valid")) {
                // Пользователь успешно залогинился
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("✅ Authentication successful! Now you can get events.");

                // Добавляем кнопку для следующего действия
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setOneTimeKeyboard(true);

                KeyboardRow row = new KeyboardRow();
                row.add("/getEvent");  // Следующая команда

                keyboardMarkup.setKeyboard(Collections.singletonList(row));
                message.setReplyMarkup(keyboardMarkup);

                sendMessage(message);
            }

            if (messageText.equals("/start")) {
                SendMessage message = sendLoginButton(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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

    public SendMessage sendLoginButton(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please log in using Telegram:");

        // Create the login button
        InlineKeyboardButton loginButton = new InlineKeyboardButton();
        loginButton.setText("Login with Telegram");
        loginButton.setUrl(
                "https://oauth.telegram.org/" +
                        "auth?bot_id=7941376949&origin=https://musicevent.com" +
//                        "&return_to=https://phet-dev.colorado.edu/html/build-an-atom/0.0.0-3/simple-text-only-test-page.html");
                        "&return_to=https://5472-138-199-47-209.ngrok-free.app/telegram-login.html");

        // Attach button to the message
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(Collections.singletonList(loginButton)));
        message.setReplyMarkup(markup);

        return message;
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
