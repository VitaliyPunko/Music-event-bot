package vpunko.musiceventbot.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessageRequestEvent {

    long chatId;
    String message;
}