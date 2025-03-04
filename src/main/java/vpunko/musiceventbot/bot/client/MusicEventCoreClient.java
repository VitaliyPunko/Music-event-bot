package vpunko.musiceventbot.bot.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import vpunko.musiceventbot.bot.dto.MusicEventDto;
import vpunko.musiceventbot.bot.exception.MusicEventCoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Slf4j
@Component
public class MusicEventCoreClient {

    private final RestClient restClient;
    private final String clientId;

    public MusicEventCoreClient(
            RestClient.Builder restClient,
            @Value("${application.urls.music-event-core}") String spotifyBaseUrl,
            @Value("${spring.security.oauth2.client.registration.music-event-bot.client-id}") String clientId
    ) {
        this.clientId = clientId;
        this.restClient = restClient
                .baseUrl(spotifyBaseUrl)
                .build();
    }

    public List<MusicEventDto> getMusicEventByArtist(String artist) {
        MusicEventDto[] body = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getEventByArtist")
                        .queryParam("artist", artist).build()
                )
                .attributes(clientRegistrationId(clientId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new MusicEventCoreException(
                            response.getStatusCode(),
                            response.getHeaders(),
                            response.getBody().toString()
                    );
                })
                .body(MusicEventDto[].class);

        if (body == null || body.length == 0) {
            log.error("MusicEvenCore API getMusicEventByArtist return null body");
            return new ArrayList<>();
        }
        return  Arrays.asList(body);
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
