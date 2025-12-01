package vpunko.musiceventbot.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessageRequestEvent {

    private long chatId;
    private String message;
    private boolean forTest;
}