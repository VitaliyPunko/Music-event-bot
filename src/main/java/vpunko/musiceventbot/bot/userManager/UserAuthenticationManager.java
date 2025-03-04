package vpunko.musiceventbot.bot.userManager;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserAuthenticationManager {

    private Map<Long, Boolean> authenticatedUsers = new ConcurrentHashMap<>();

    public void authenticateUser(long chatId) {
        authenticatedUsers.put(chatId, true);
    }

    public void logoutUser(long chatId) {
        authenticatedUsers.put(chatId, false);
    }

    public boolean isUserAuthenticated(long chatId) {
        return authenticatedUsers.getOrDefault(chatId, false);
    }
}