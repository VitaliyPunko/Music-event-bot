package vpunko.musiceventbot.bot.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MusicEventDto {

    private String id;
    private String title;
    private String artist;
    private OffsetDateTime eventDate;

}
