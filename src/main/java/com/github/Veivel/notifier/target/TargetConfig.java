package com.github.Veivel.notifier.target;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public interface TargetConfig {
    String name();
    boolean enabled();
}
