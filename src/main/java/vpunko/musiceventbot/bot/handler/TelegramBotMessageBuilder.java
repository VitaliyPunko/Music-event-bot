package vpunko.musiceventbot.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import vpunko.musiceventbot.bot.BotUserState;
import vpunko.musiceventbot.bot.TelegramAuthService;
import vpunko.musiceventbot.bot.UserStateManager;

import java.util.ArrayList;
import java.util.List;

import static vpunko.musiceventbot.bot.BotUserState.LOGIN_REQUESTED;

@Service
@RequiredArgsConstructor
public class TelegramBotMessageBuilder {

    private final UserStateManager userStateManager;
    private final TelegramAuthService telegramAuthService;

    public String handleUserInput(long chatId, String messageText) {
        BotUserState userState = userStateManager.getUserState(chatId);

        if (messageText.equals("/start")) {
            userStateManager.setUserState(chatId, LOGIN_REQUESTED);
            return "Please, login via your telegram account";
        }
        return "";
    }


    public InlineKeyboardButton loginButton() {
        InlineKeyboardButton loginButton = new InlineKeyboardButton();
        loginButton.setText("Login with Telegram");
        //for local test run ngrok and change url here and in telegram.html. And set this url as domain in Telegram
        loginButton.setUrl("https://53dd-138-199-47-215.ngrok-free.app/auth/telegram");
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
