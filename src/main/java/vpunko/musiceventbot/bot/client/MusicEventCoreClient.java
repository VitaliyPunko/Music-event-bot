package vpunko.musiceventbot.bot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Component
public class MusicEventCoreClient {

    private final RestClient restClient;

    public MusicEventCoreClient(
            RestClient.Builder restClient,
            @Value("${application.urls.music-event-core}") String spotifyBaseUrl
    ) {
        this.restClient = restClient
                .baseUrl(spotifyBaseUrl)
                .build();
    }

    public String getMusicEventByArtist(String artist) {
        String body = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getEventByArtist")
                        .queryParam("artist", artist).build()
                )
                .attributes(clientRegistrationId("music-event-bot"))
                .retrieve()
                .body(String.class);
        return body;
    }

//    public MusicEventDto getMusicEventByArtist(String artist) {
//        MusicEventDto body = restClient.get()
//                .uri(uriBuilder -> uriBuilder.path("/getEventByArtist")
//                        .queryParam("artist", artist).build()
//                )
//                .attributes(clientRegistrationId("music-event-bot"))
//                //  .header("Authorization", "Bearer " + accessToken)  interceptor past the token
//                .retrieve()
//                .body(MusicEventDto.class);
//        return body;
//    }
}
