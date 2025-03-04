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
    public static final String A_CLOSE_TAG = "</a>";
    private static final String b_tag = "<b>";
    private static final String b_close_tag = "</b>";
    private static final String LINE_BREAK = "\n";
    private static final String A_HREF = "<a href=\"";
    private static final String A_HREF_CLOSE = "\">";

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

        sb.append("🎤 ").append(b_tag).append("Event Name").append(b_close_tag).append(": ").append(event.getName()).append(LINE_BREAK);
        sb.append("📅 ").append(b_tag).append("Date").append(b_close_tag).append(": ")
                .append(event.getStartTime().toString()).append(event.getTimeZone()).append(LINE_BREAK);
        sb.append("💰 ").append(b_tag).append("Price").append(b_close_tag).append(": ")
                .append(event.getPrice() != null ? event.getPrice() + " (" + event.getCurrency() + ")" : "unknown").append(LINE_BREAK);
        sb.append("🎟️ ").append(b_tag).append("Ticket Status").append(b_close_tag).append(": ").append(event.getTicketSaleStatus()).append(LINE_BREAK);

        if (event.getVenue() != null) {
            sb.append("📍 ").append(b_tag).append("Venue").append(b_close_tag).append(": ").append(event.getVenue().getName()).append(", ")
                    .append(event.getVenue().getCity()).append(", ")
                    .append(event.getVenue().getCountry()).append(LINE_BREAK);
            sb.append("📌 ").append(b_tag).append("Address").append(b_close_tag).append(": ").append(event.getVenue().getAddress()).append(LINE_BREAK);
            //  sb.append("🔗 ").append(b_tag).append("Venue URL").append(b_close_tag).append(": ").append("[Click here](").append(event.getVenue().getUrl()).append(")\n");
            sb.append("🔗 ").append(b_tag).append("Venue URL").append(b_close_tag).append(": ").append(A_HREF).append(event.getVenue().getUrl()).append(A_HREF_CLOSE)
                    .append("[Click here]").append(A_CLOSE_TAG).append(LINE_BREAK);
        }

        if (event.getImageUrl() != null) {
            sb.append("📷 " + b_tag + "Image" + b_close_tag + ":").append(A_HREF).append(event.getImageUrl()).append(A_HREF_CLOSE)
                    .append(" [View Image]").append(A_CLOSE_TAG).append(LINE_BREAK);
        }

        if (event.getDescription() != null) {
            sb.append("📝 " + b_tag + "Description" + b_close_tag + ": ").append(event.getDescription()).append(LINE_BREAK);
        }

        return sb.toString();
    }

}
