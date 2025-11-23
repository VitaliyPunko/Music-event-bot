package vpunko.musiceventbot.bot.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "telegram")
public class BotConfig {

    private String webhookUrl;

    public String getWebhookUrl() {
        return webhookUrl + "/webhook";
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
