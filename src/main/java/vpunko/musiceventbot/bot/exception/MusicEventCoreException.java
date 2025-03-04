package vpunko.musiceventbot.bot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class MusicEventCoreException extends RuntimeException {

    private HttpStatusCode statusCode;
    private HttpHeaders headers;
    private String responseBody;

    public MusicEventCoreException(String message) {
        super(message);
    }

    public MusicEventCoreException(HttpStatusCode statusCode, HttpHeaders headers, String responseBody) {
        super(String.format("MusicEvenCore API request failed with status code: %s, headers: %s, response body: %s",
                statusCode, headers, responseBody));
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }
}


