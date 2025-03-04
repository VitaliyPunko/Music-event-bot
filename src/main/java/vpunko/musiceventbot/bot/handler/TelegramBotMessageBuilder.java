package vpunko.musiceventbot.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import vpunko.musiceventbot.bot.BotUserState;
import vpunko.musiceventbot.bot.UserStateManager;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotMessageBuilder {

    private final UserStateManager userStateManager;
    @Value("${application.urls.music-event-auth-https}")
    private String MUSIC_EVENT_AUTH_HTTP;

    public String handleUserInput(long chatId, String messageText) {
        BotUserState userState = userStateManager.getUserState(chatId);

        return "";
    }


    public InlineKeyboardButton loginButton() {
        InlineKeyboardButton loginButton = new InlineKeyboardButton();
        loginButton.setText("Login with Telegram");
        //for local test run ngrok and change url here and in telegram.html. And set this url as domain in Telegram
        loginButton.setUrl(MUSIC_EVENT_AUTH_HTTP.concat("/auth/telegram"));
        return loginButton;
    }

    public ReplyKeyboardMarkup getReplyKeyboardMarkup(String ... linesName) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        for (String line : linesName) {
            KeyboardRow row = new KeyboardRow();
            row.add(line);
            keyboardRowList.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRowList);
        return keyboardMarkup;
    }

}
