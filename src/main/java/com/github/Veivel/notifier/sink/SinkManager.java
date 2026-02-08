package com.github.Veivel.notifier.sink;

import com.github.Veivel.config.ModConfig;
import java.util.ArrayList;
import java.util.List;

public class SinkManager {

    private static List<AbstractSink> sinks = new ArrayList<AbstractSink>();

    private SinkManager() {}

    public static void init(ModConfig config) {
        if (config.isSendToConsole()) {
            ConsoleSink consoleSink = new ConsoleSink();
            sinks.add(consoleSink);
        }
        if (config.isSendToIngame()) {
            ChatSink chatSink = new ChatSink();
            sinks.add(chatSink);
        }
        if (config.isSendToDiscord()) {
            DiscordWebhookSink discordSink = new DiscordWebhookSink(
                config.getDiscordWebhookUrl()
            );
            discordSink.testConnection();
            sinks.add(discordSink);
        }
    }

    public static void emit(
        String playerName,
        int quantity,
        int x,
        int y,
        int z,
        String dimension
    ) {
        for (AbstractSink sink : sinks) {
            sink.readOut(playerName, quantity, x, y, z, dimension);
        }
    }
}
