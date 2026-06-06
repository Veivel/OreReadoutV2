package com.github.Veivel.notifier.target.console;

import com.github.Veivel.notifier.target.TargetConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record ServerConsoleConfig(
    String name,
    boolean enabled
) implements TargetConfig {}
