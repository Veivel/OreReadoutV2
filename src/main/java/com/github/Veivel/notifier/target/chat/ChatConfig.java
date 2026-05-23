package com.github.Veivel.notifier.target.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.Veivel.notifier.target.TargetConfig;

public record ChatConfig(
    String name,
    boolean enabled,

    @JsonProperty("notification_sound") boolean notificationSound
) implements TargetConfig {}
