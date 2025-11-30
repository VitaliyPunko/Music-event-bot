package vpunko.musiceventbot.bot.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketMasterResponseEvent {

    private List<TicketmasterEvent> events;
    private long chatId;
}
