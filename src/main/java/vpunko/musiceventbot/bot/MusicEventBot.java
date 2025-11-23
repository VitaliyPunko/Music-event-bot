package vpunko.musiceventbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vpunko.musiceventbot.bot.exception.TelegramMessageSendException;
import vpunko.musiceventbot.bot.handler.MusicEventCoreHandler;
import vpunko.musiceventbot.bot.handler.TelegramBotMessageBuilder;
import vpunko.musiceventbot.bot.userManager.UserAuthenticationManager;
import vpunko.musiceventbot.bot.userManager.UserStateManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vpunko.musiceventbot.bot.BotUserState.INCORRECT_ARTIST_NAME;

@Slf4j
@Service
public class MusicEventBot extends TelegramWebhookBot {

    public static final String FIND_YOUR_FAVORITE_ARTISTS_EVENT = "Find your favorite artist's event";
    public static final String TRY_TO_FIND_ANOTHER_ARTISTS_EVENT = "Try to find another artist's event";
    public static final String FINISH = "Finish";
    public static final String MESSAGE_TEXT = "messageText";
    public static final String CHAT_ID = "chatId";


    private final String botUsername;
    private final String botToken;
    private final TelegramBotMessageBuilder botMessageBuilder;
    private final MusicEventCoreHandler coreHandler;
    private final UserStateManager userStateManager;
    private final UserAuthenticationManager authenticationManager;

    public MusicEventBot(
            @Value("${telegram.bot_username}") String botUsername,
            @Value("${telegram.bot_token}") String botToken,
            TelegramBotMessageBuilder botMessageBuilder,
            MusicEventCoreHandler coreHandler,
            UserStateManager userStateManager,
            UserAuthenticationManager authenticationManager) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botMessageBuilder = botMessageBuilder;
        this.coreHandler = coreHandler;
        this.userStateManager = userStateManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() || update.hasCallbackQuery()) {
            String messageText;
            long chatId;
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Map<String, String> dataFromCallBack = getDataFromCallBack(callbackQuery);
                messageText = dataFromCallBack.get(MESSAGE_TEXT);
                chatId = Long.parseLong(dataFromCallBack.get(CHAT_ID));
            } else {
                messageText = update.getMessage().getText();
                chatId = update.getMessage().getChatId();
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            BotUserState userState = userStateManager.getUserState(chatId);


            if (messageText.equals("/start")) {
                if (authenticationManager.isUserAuthenticated(chatId)) {
                    message.setText("✅ I saw you passed authentication before. Press /next to continue");
                    sendMessage(message);
                } else {
                    message.setText("Click the button below to log in:");
                    var loginButton = botMessageBuilder.loginButton();
                    var markup = new InlineKeyboardMarkup();
                    markup.setKeyboard(List.of(List.of(loginButton)));

                    message.setReplyMarkup(markup);
                    sendMessage(message);

                    userStateManager.setUserState(chatId, BotUserState.START);
                }

            } else if (messageText.equals("/next")) {
                // User was successfully authenticated
                if (authenticationManager.isUserAuthenticated(chatId)) {
                    message.setText("Now you can get events.");
                } else {
                    message.setText("✅ Authentication successful! Now you can get events.");
                }

                // Add two buttons for the next option
                ReplyKeyboardMarkup keyboardMarkup = botMessageBuilder.getReplyKeyboardMarkup(
                        FIND_YOUR_FAVORITE_ARTISTS_EVENT
                );
                message.setReplyMarkup(keyboardMarkup);
                sendMessage(message);

                userStateManager.setUserState(chatId, BotUserState.AUTHENTICATED);
                authenticationManager.authenticateUser(chatId);

            } else if (messageText.equals(FIND_YOUR_FAVORITE_ARTISTS_EVENT)) {
                message.setText("Please, enter the name of the artist or band");
                sendMessage(message);
                userStateManager.setUserState(chatId, BotUserState.WAITING_FOR_ARTIST_NAME);

            } else if (userState == BotUserState.WAITING_FOR_ARTIST_NAME) {
                //logic to call music-event-service
                handleArtistNameInput(messageText, message, chatId);
            } else if (userState == BotUserState.INCORRECT_ARTIST_NAME) {
                handleArtistNameInput(messageText, message, chatId);
            } else if (userState == BotUserState.GET_EVENT) {

                if (messageText.equals(TRY_TO_FIND_ANOTHER_ARTISTS_EVENT)) {
                    message.setText("Please, enter the name of the artist or band");
                    sendMessage(message);
                    userStateManager.setUserState(chatId, BotUserState.WAITING_FOR_ARTIST_NAME);

                } else if (messageText.equals(FINISH)) {
                    userStateManager.setUserState(chatId, BotUserState.DONE);
                    message.setText("Press the button to start again");
                    // Add two buttons for the next option
                    var startButton = botMessageBuilder.startButton();
                    var markup = new InlineKeyboardMarkup();
                    markup.setKeyboard(List.of(List.of(startButton)));

                    message.setReplyMarkup(markup);
                    sendMessage(message);
                }

            }
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return "";
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText() || update.hasCallbackQuery()) {
//            String messageText;
//            long chatId;
//            if (update.hasCallbackQuery()) {
//                CallbackQuery callbackQuery = update.getCallbackQuery();
//                Map<String, String> dataFromCallBack = getDataFromCallBack(callbackQuery);
//                messageText = dataFromCallBack.get(MESSAGE_TEXT);
//                chatId = Long.parseLong(dataFromCallBack.get(CHAT_ID));
//            } else {
//                messageText = update.getMessage().getText();
//                chatId = update.getMessage().getChatId();
//            }
//
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            BotUserState userState = userStateManager.getUserState(chatId);
//
//
//            if (messageText.equals("/start")) {
//                if (authenticationManager.isUserAuthenticated(chatId)) {
//                    message.setText("✅ I saw you passed authentication before. Press /next to continue");
//                    sendMessage(message);
//                } else {
//                    message.setText("Click the button below to log in:");
//                    var loginButton = botMessageBuilder.loginButton();
//                    var markup = new InlineKeyboardMarkup();
//                    markup.setKeyboard(List.of(List.of(loginButton)));
//
//                    message.setReplyMarkup(markup);
//                    sendMessage(message);
//
//                    userStateManager.setUserState(chatId, BotUserState.START);
//                }
//
//            } else if (messageText.equals("/next")) {
//                // User was successfully authenticated
//                if (authenticationManager.isUserAuthenticated(chatId)) {
//                    message.setText("Now you can get events.");
//                } else {
//                    message.setText("✅ Authentication successful! Now you can get events.");
//                }
//
//                // Add two buttons for the next option
//                ReplyKeyboardMarkup keyboardMarkup = botMessageBuilder.getReplyKeyboardMarkup(
//                        FIND_YOUR_FAVORITE_ARTISTS_EVENT
//                );
//                message.setReplyMarkup(keyboardMarkup);
//                sendMessage(message);
//
//                userStateManager.setUserState(chatId, BotUserState.AUTHENTICATED);
//                authenticationManager.authenticateUser(chatId);
//
//            } else if (messageText.equals(FIND_YOUR_FAVORITE_ARTISTS_EVENT)) {
//                message.setText("Please, enter the name of the artist or band");
//                sendMessage(message);
//                userStateManager.setUserState(chatId, BotUserState.WAITING_FOR_ARTIST_NAME);
//
//            } else if (userState == BotUserState.WAITING_FOR_ARTIST_NAME) {
//                //logic to call music-event-service
//                handleArtistNameInput(messageText, message, chatId);
//            } else if (userState == BotUserState.INCORRECT_ARTIST_NAME) {
//                handleArtistNameInput(messageText, message, chatId);
//            } else if (userState == BotUserState.GET_EVENT) {
//
//                if (messageText.equals(TRY_TO_FIND_ANOTHER_ARTISTS_EVENT)) {
//                    message.setText("Please, enter the name of the artist or band");
//                    sendMessage(message);
//                    userStateManager.setUserState(chatId, BotUserState.WAITING_FOR_ARTIST_NAME);
//
//                } else if (messageText.equals(FINISH)) {
//                    userStateManager.setUserState(chatId, BotUserState.DONE);
//                    message.setText("Press the button to start again");
//                    // Add two buttons for the next option
//                    var startButton = botMessageBuilder.startButton();
//                    var markup = new InlineKeyboardMarkup();
//                    markup.setKeyboard(List.of(List.of(startButton)));
//
//                    message.setReplyMarkup(markup);
//                    sendMessage(message);
//                }
//
//            }
//        }
//    }

    private void handleArtistNameInput(String artistName, SendMessage message, long chatId) {
        //logic to call music-event-service
        List<String> eventByArtist = coreHandler.getMusicEventByArtist(artistName);

        if (eventByArtist.isEmpty()) {
            userStateManager.setUserState(chatId, INCORRECT_ARTIST_NAME);
            message.setText("Couldn't find any event. Please, check your spelling and enter the name again");
            sendMessage(message);
        } else {
            sendEventsToUser(chatId, eventByArtist);
            userStateManager.setUserState(chatId, BotUserState.GET_EVENT);

            // Add two buttons for the next option
            ReplyKeyboardMarkup keyboardMarkup = botMessageBuilder.getReplyKeyboardMarkup(
                    TRY_TO_FIND_ANOTHER_ARTISTS_EVENT,
                    FINISH
            );
            message.setReplyMarkup(keyboardMarkup);
            message.setText("You can see info about the events above. Please choose from the following options");
            sendMessage(message);
        }
    }


    public void sendEventsToUser(long chatId, List<String> events) {
        for (String event : events) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(event);
            sendMessage.setParseMode("HTML"); // Enable HTML formatting

            sendMessage(sendMessage);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred during sendMessage method {}", e.getMessage());
            throw new TelegramMessageSendException(e.getMessage());
        }
    }

    private Map<String, String> getDataFromCallBack(CallbackQuery callbackQuery) {
        Map<String, String> map = new HashMap<>();
        if (callbackQuery.getMessage() != null) {
            map.put(MESSAGE_TEXT, callbackQuery.getData());
            map.put(CHAT_ID, callbackQuery.getMessage().getChatId().toString());
            return map;
        }
        log.error("Error occurred during callBack");
        throw new TelegramMessageSendException("Error occurred during callBack");

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
