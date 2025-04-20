package com.github.Veivel.notifier;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.orereadout.OreReadoutMod;

public class DiscordWebhookSender {
    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private String webhookUrl = "";

    public DiscordWebhookSender(String webhookUrl) {
      this.webhookUrl = webhookUrl;
    }

    private void sendPayload(String payloadString) {
        try {
            // Send the JSON payload via HTTP POST
            URL url = new URI(this.webhookUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payloadString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
    
            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                LOGGER.error("Webhook request failed with response code: {}", responseCode);
            }
    
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testWebhook() {
        StringBuilder jsonPayload = new StringBuilder();
        jsonPayload.append("{")
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
    public void readOut(String playerName, int quantity, int x, int y, int z, String dimension) {
        // build the JSON payload manually. see: https://toolscord.com/webhook
        StringBuilder jsonPayload = new StringBuilder();
        jsonPayload.append("{")
            .append("\"embeds\": [")
                .append("{")
                    .append("\"title\": \"\",")
                    .append("\"description\": \"")
                        .append(escapeJson(playerName))
                        .append(" mined ")
                        .append(quantity)
                        .append(" ores at [`")
                        .append(x)
                        .append(" ")
                        .append(y)
                        .append(" ")
                        .append(z)
                        .append("`]")
                        .append(" in ")
                        .append(dimension)
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

    /**
     * Escapes special characters in a JSON string.
     */
    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        // Replace backslashes and double quotes.
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}