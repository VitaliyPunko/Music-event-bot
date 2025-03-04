package vpunko.musiceventbot.bot.dto;

import lombok.Data;
import vpunko.musiceventbot.bot.constant.TicketSaleEnum;

import java.time.OffsetDateTime;

@Data
public class MusicEventDto {

    /**
     * Event name
     */
    private String name;
    /**
     * Event date and time
     */
    private OffsetDateTime startTime;
    /**
     * Event timezone
     */
    private String timeZone;
    /**
     * Price
     */
    private Double price;
    /**
     * Currency
     */
    private String currency;
    /**
     * Event ticket status:
     */
    private TicketSaleEnum ticketSaleStatus;
    /**
     * Event description
     */
    private String description;
    /**
     * Event venue url:
     */
    private String venuesUrl;
    /**
     * Start date of the ticket sale
     */
    private String saleStartDateTime;
    /**
     * End date of the ticket sale
     */
    private String saleEndDateTime;
    /**
     * Event image
     */
    private String imageUrl;
    /**
     * Event venue
     */
    private MusicEventVenue venue;


    @Data
    public static class MusicEventVenue {
        private String name;
        private String country;
        private String city;
        private String address;
        private String url;
    }
}
