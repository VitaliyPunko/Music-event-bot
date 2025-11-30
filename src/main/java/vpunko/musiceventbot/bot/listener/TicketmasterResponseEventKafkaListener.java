package vpunko.musiceventbot.bot.listener;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import vpunko.musiceventbot.bot.MusicEventBot;
import vpunko.musiceventbot.bot.dto.TicketMasterResponseEvent;

import static vpunko.musiceventbot.bot.constant.MetricsConstant.TICKET_MASTER_RESPONSE_EVENT_LISTENER_COUNTER;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketmasterResponseEventKafkaListener {

    private final MusicEventBot musicEventBot;

    @KafkaListener(topics = "${spring.kafka.in.ticket-master-response-event.topic}")
    @Counted(value = TICKET_MASTER_RESPONSE_EVENT_LISTENER_COUNTER)
    void listener(@Payload TicketMasterResponseEvent data) {
        log.info("Received message [{}] from ticket-master-response-topic }", data);

        SendMessage message = new SendMessage();
        message.setChatId(data.getChatId());
        musicEventBot.handleKafkaArtistNameInput(message, data.getChatId(), data.getEvents());
    }

}
