package vpunko.musiceventbot.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vpunko.musiceventbot.bot.BotUserState;
import vpunko.musiceventbot.bot.TelegramAuthService;
import vpunko.musiceventbot.bot.UserStateManager;

import static vpunko.musiceventbot.bot.BotUserState.*;

@Service
@RequiredArgsConstructor
public class UserInputHandler {

    private final UserStateManager userStateManager;
    private final TelegramAuthService telegramAuthService;

    public String handleUserInput(long chatId, String messageText) {
        BotUserState userState = userStateManager.getUserState(chatId);

        if (messageText.equals("/start")) {
            userStateManager.setUserState(chatId, LOGIN_REQUESTED);
            return "Please enter your login:";
        } else if (userState.equals(LOGIN_REQUESTED)) {
            // Store the login and ask for the password
            userStateManager.setUserLogin(chatId, messageText);
            userStateManager.setUserState(chatId, PASSWORD_REQUESTED);
            return "Please enter your password:";
        } else if (userState.equals(PASSWORD_REQUESTED)) {
            // Validate credentials
            String login = userStateManager.getUserLogin(chatId);
            String password = messageText;

       //     boolean isValid = validateCredentials(login, password);
            boolean isValid = true;
            if (isValid) {
                userStateManager.setUserState(chatId, AUTHENTICATED);
                return String.format(
                        "Login successful! Hello %s What would you like to do next? (e.g., /getEvents)",
                        login);
            } else {
                userStateManager.clearUserState(chatId);
                return "Invalid credentials. Please try again.";
            }
        } else if (messageText.equals("/getEvents") && userState.equals(AUTHENTICATED)) {
            // Fetch events and return JSON
            String artist = "artist";
            String eventsJson = fetchEventsByArtist(artist);
            return formatJsonForUser(eventsJson);
        } else {
            return "Unknown command. Please try again.";
        }
    }

//    public boolean validateCredentials(String login, String password) {
//        return telegramAuthService.validateCredentials(login, password);
//    }

    public String fetchEventsByArtist(String artist) {
        // Call your API or database to fetch events
        return "[{\"event\": \"Concert\", \"date\": \"2023-12-25\"}]"; // Replace with actual data
    }

    private String formatJsonForUser(String json) {
        // Parse JSON and format it into a readable string
        return "Here are the events:\n" + json; // Replace with actual formatting logic
    }
}
