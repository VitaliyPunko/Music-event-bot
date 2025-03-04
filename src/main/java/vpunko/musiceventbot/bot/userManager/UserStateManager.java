package vpunko.musiceventbot.bot.userManager;

import org.springframework.stereotype.Component;
import vpunko.musiceventbot.bot.BotUserState;

import java.util.HashMap;
import java.util.Map;

import static vpunko.musiceventbot.bot.BotUserState.DEFAULT;

@Component
public class UserStateManager {

    private Map<Long, BotUserState> userStates = new HashMap<>();

    public void setUserState(long chatId, BotUserState state) {
        userStates.put(chatId, state);
    }

    public BotUserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, DEFAULT);
    }

    public void clearUserState(long chatId) {
        userStates.remove(chatId);
    }
}
