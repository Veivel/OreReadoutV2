package com.github.Veivel.notifier.target.discord;

import com.github.Veivel.notifier.target.TargetConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record DiscordConfig(
    String name,
    boolean enabled,
    @Setting("webhook_url") String webhookUrl
) implements TargetConfig {}
