package com.github.Veivel.config;

import com.github.Veivel.notifier.target.TargetConfig;
import java.util.List;
import java.util.Set;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * An immutable record containing the mod's Config object.
 * It is not persistent; the record will be replaced with a new on reload.
 * <br/>
 * All error-handling, loading, and serializing are handled by the Manager.
 */
@ConfigSerializable
public record ModConfig(
    @Setting("config_version") Integer configVersion,

    @Setting("targets") List<TargetConfig> targets,

    @Setting("readout_blocks") Set<String> readoutBlockSet,

    @Setting("readout_window_in_seconds") Integer readoutWindowInSeconds,

    @Setting("blocks_broken_threshold") Integer blocksBrokenThreshold,

    @Setting("debug_mode") boolean debugMode
) {}
