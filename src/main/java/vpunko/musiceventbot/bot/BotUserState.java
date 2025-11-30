package vpunko.musiceventbot.bot;

public enum BotUserState {

    DEFAULT,
    START,
    AUTHENTICATED,
    WAITING_FOR_ARTIST_NAME,
    INCORRECT_ARTIST_NAME,
    WAITING_ARTIST_EVENTS,
    GET_EVENT,
    DONE
}
