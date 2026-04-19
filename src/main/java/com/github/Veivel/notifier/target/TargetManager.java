package com.github.Veivel.notifier.target;

import com.github.Veivel.config.ModConfig;
import com.github.Veivel.event.ReadoutEvent;
import java.util.ArrayList;
import java.util.List;

public class TargetManager {

    private static List<AbstractTarget> targets = new ArrayList<AbstractTarget>();

    private TargetManager() {}

    public static void init(ModConfig config) {
        if (config.isSendToConsole()) {
            ServerConsoleTarget consoleTarget = new ServerConsoleTarget();
            targets.add(consoleTarget);
        }
        if (config.isSendToIngame()) {
            ChatTarget chatTarget = new ChatTarget();
            targets.add(chatTarget);
        }
        if (config.isSendToDiscord()) {
            DiscordTarget discordTarget = new DiscordTarget(
                config.getDiscordWebhookUrl()
            );
            discordTarget.testConnection();
            targets.add(discordTarget);
        }
    }

    public static void emit(ReadoutEvent event) {
        for (AbstractTarget target : targets) {
            target.sendReadout(event);
        }
    }
}
