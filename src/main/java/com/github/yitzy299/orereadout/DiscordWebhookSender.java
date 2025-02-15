package main.java.com.github.yitzy299.orereadout;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookSender {

    public static void sendWebhook(String playerName, String minedItem, int x, int y, int z, String webhookUrl) {
        try {
            // Build the JSON payload manually.
            StringBuilder jsonPayload = new StringBuilder();
            jsonPayload.append("{")
                .append("\"embeds\": [")
                    .append("{")
                        .append("\"title\": \"Ore Readout Event\",")
                        .append("\"description\": \"")
                            .append(escapeJson(playerName))
                            .append(" has mined ")
                            .append(escapeJson(minedItem))
                            .append(" at [ ")
                            .append(x)
                            .append(" ")
                            .append(y)
                            .append(" ")
                            .append(z)
                            .append(" ].\",")
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
            URL url = new URL(webhookUrl);
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