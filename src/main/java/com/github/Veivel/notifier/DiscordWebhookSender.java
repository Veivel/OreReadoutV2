package com.github.Veivel.notifier;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookSender {
    private String webhookUrl = "";

    public DiscordWebhookSender(String webhookUrl) {
      this.webhookUrl = webhookUrl;
    }

    public void sendReadout(String playerName, String minedItem, int x, int y, int z, String dimension) {
        try {
            // build the JSON payload manually. see: https://toolscord.com/webhook
            StringBuilder jsonPayload = new StringBuilder();
            jsonPayload.append("{")
                .append("\"embeds\": [")
                    .append("{")
                        .append("\"title\": \"\",")
                        .append("\"description\": \"")
                            .append(escapeJson(playerName))
                            .append(" mined ")
                            .append(escapeJson(minedItem))
                            .append(" at [`")
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

            // Send the JSON payload via HTTP POST
            URL url = new URI(webhookUrl).toURL();
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
                // todo: use logger
                System.err.println("Webhook request failed with response code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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