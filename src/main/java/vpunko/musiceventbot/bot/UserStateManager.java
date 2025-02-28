package vpunko.musiceventbot.bot;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static vpunko.musiceventbot.bot.BotUserState.DEFAULT;

@Component
public class UserStateManager {

    private Map<Long, BotUserState> userStates = new HashMap<>();
    private Map<Long, String> userLogins = new HashMap<>();

    public void setUserState(long chatId, BotUserState state) {
        userStates.put(chatId, state);
    }

    public BotUserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, DEFAULT);
    }

    public void setUserLogin(long chatId, String login) {
        userLogins.put(chatId, login);
    }

    public String getUserLogin(long chatId) {
        return userLogins.get(chatId);
    }

    public void clearUserState(long chatId) {
        userStates.remove(chatId);
        userLogins.remove(chatId);
    }
}
