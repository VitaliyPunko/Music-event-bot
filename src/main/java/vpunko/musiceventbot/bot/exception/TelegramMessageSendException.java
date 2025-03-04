package vpunko.musiceventbot.bot.exception;

public class TelegramMessageSendException extends RuntimeException {

    public TelegramMessageSendException(String message) {
        super(message);
    }
}
