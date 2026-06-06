package com.github.Veivel.notifier.target.chat;

import com.github.Veivel.notifier.target.TargetConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record ChatConfig(
    String name,
    boolean enabled,
    @Setting("notification_sound") boolean notificationSound
) implements TargetConfig {}
