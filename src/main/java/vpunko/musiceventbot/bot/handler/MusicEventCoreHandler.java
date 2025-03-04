package vpunko.musiceventbot.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vpunko.musiceventbot.bot.client.MusicEventCoreClient;
import vpunko.musiceventbot.bot.dto.MusicEventDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicEventCoreHandler {

    private final MusicEventCoreClient musicEventCoreClient;

    public List<String> getMusicEventByArtist(String artist) {
        List<MusicEventDto> musicEventByArtist = musicEventCoreClient.getMusicEventByArtist(artist);

        return formatEvents(musicEventByArtist);
    }

    private List<String> formatEvents(List<MusicEventDto> events) {
        List<String> messages = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < events.size(); i++) {
            sb.append(formatEvent(events.get(i))).append("\n");

            // Split into multiple messages if necessary
            if ((i + 1) % 5 == 0 || i == events.size() - 1) {
                messages.add(sb.toString());
                sb = new StringBuilder();
            }
        }

        return messages;
    }

    private String formatEvent(MusicEventDto event) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎤 ").append("<b>Event Name</b>: ").append(event.getName()).append("\n");
        sb.append("📅 ").append("<b>Date</b>: ")
                .append(event.getStartTime().toString()).append(event.getTimeZone()).append("\n");
        sb.append("💰 ").append("<b>Price</b>: ")
                .append(event.getPrice() != null ? event.getPrice() + " (" + event.getCurrency() + ")" : "unknown").append("\n");
        sb.append("🎟️ ").append("<b>Ticket Status</b>: ").append(event.getTicketSaleStatus()).append("\n");

        if (event.getVenue() != null) {
            sb.append("📍 ").append("<b>Venue</b>: ").append(event.getVenue().getName()).append(", ")
                    .append(event.getVenue().getCity()).append(", ")
                    .append(event.getVenue().getCountry()).append("\n");
            sb.append("📌 ").append("<b>Address</b>: ").append(event.getVenue().getAddress()).append("\n");
            sb.append("🔗 ").append("<b>Venue URL</b>: ").append("[Click here](").append(event.getVenue().getUrl()).append(")\n");
        }

        if (event.getImageUrl() != null) {
            sb.append("📷 <b>Image</b>:").append("<a href=\"").append(event.getImageUrl()).append("\"> [View Image]").append("</a>").append("\n");
        }

        if (event.getDescription() != null) {
            sb.append("📝 <b>Description</b>: ").append(event.getDescription()).append("\n");
        }

        return sb.toString();
    }

}
