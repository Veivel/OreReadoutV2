package com.github.Veivel.notifier.target.discord;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.Target;
import com.github.Veivel.notifier.target.TargetConfig;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.util.DataFormat;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordTarget implements Target {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private HttpClient httpClient; // reusable HttpClient instance
    private URI webhookUri;
    private DiscordConfig config;

    public DiscordTarget(DiscordConfig config) {
        try {
            this.config = config;
            this.webhookUri = new URI(config.webhookUrl());
            this.httpClient = HttpClient.newHttpClient();
        } catch (URISyntaxException e) {
            logger.error(e);
        }
    }

    /**
     * The underlying method that sends the payload `payloadString`
     * as a POST request to the webhook URL determined during
     * initialization.
     */
    private void sendPayload(String payloadString) {
        try {
            // Build HTTP request with JSON payload and timeout
            HttpRequest request = HttpRequest.newBuilder()
                .uri(webhookUri)
                .headers("Content-Type", "application/json")
                .headers("Sender-Application", "ore-readout-v2")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(payloadString))
                .build();

            // Send the HTTP POST request asynchronously
            httpClient
                .sendAsync(request, BodyHandlers.ofString())
                .handleAsync((response, exception) -> {
                    // Log response or error asynchronously
                    if (exception != null) {
                        logger.error(
                            "Failed to send Discord webhook, exception: {}",
                            exception
                        );
                        return false;
                    } else if (
                        response.statusCode() < 200 ||
                        response.statusCode() >= 400
                    ) {
                        logger.error(
                            "Failed to send Discord webhook, response: {}",
                            response.body()
                        );
                        return false;
                    } else {
                        logger.debug(
                            "Received response from Discord webhook request: {}",
                            response.body()
                        );
                        return true;
                    }
                });

            logger.debug(
                "Sent Discord webhook! Returning without HTTP response."
            );
            return;
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void testConnection() {
        StringBuilder jsonPayload = new StringBuilder();
        jsonPayload
            .append("{")
            .append("\"embeds\": [")
            .append("{")
            .append("\"title\": \"\",")
            .append("\"description\": \"")
            .append("Ore Readout V2 was configured successfully.\",")
            .append("\"color\": 4352240,")
            .append("\"footer\": {\"text\": \"\"},")
            .append("\"author\": {\"name\": \"\"},")
            .append("\"fields\": []")
            .append("}")
            .append("],")
            .append("\"content\": \"\"")
            .append("}");

        String payloadString = jsonPayload.toString();
        sendPayload(payloadString);
    }

    /**
     * Sends a new webhook to the configured Discord Webhook URL.
     *
     * @param playerName
     * @param quantity
     * @param x
     * @param y
     * @param z
     * @param dimension
     */
    public void sendReadout(ReadoutEvent event) {
        // build the JSON payload manually. see: https://toolscord.com/webhook
        StringBuilder jsonPayload = new StringBuilder();
        jsonPayload
            .append("{")
            .append("\"embeds\": [")
            .append("{")
            .append("\"title\": \"\",")
            .append("\"description\": \"")
            .append(DataFormat.escapeJson(event.playerName))
            .append(" mined ")
            .append(event.quantity)
            .append(" ores at [`")
            .append(event.x)
            .append(" ")
            .append(event.y)
            .append(" ")
            .append(event.z)
            .append("`]")
            .append(" in ")
            .append(DataFormat.escapeJson(event.dimension))
            .append(". \",")
            .append("\"color\": 4352240,")
            .append("\"footer\": {\"text\": \"\"},")
            .append("\"author\": {\"name\": \"\"},")
            .append("\"fields\": []")
            .append("}")
            .append("],")
            .append("\"content\": \"\"")
            .append("}");

        String payloadString = jsonPayload.toString();
        sendPayload(payloadString);
    }

    @Override
    public TargetConfig getConfig() {
        return config;
    }
}
