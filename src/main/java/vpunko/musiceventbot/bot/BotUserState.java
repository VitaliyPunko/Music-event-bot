package vpunko.musiceventbot.bot;

public enum BotUserState {

    DEFAULT,
    START,
    AUTHENTICATED,
    WAITING_FOR_ARTIST_NAME,
    INCORRECT_ARTIST_NAME,
    GET_EVENT,
    DONE
}
