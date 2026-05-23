package com.github.Veivel.notifier.target.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.Veivel.notifier.target.TargetConfig;

public record DiscordConfig(
    String name,
    boolean enabled,

    @JsonProperty("webhook_url") String webhookUrl
) implements TargetConfig {}
