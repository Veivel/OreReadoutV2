package com.github.Veivel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.Veivel.notifier.target.TargetConfig;
import java.util.List;
import java.util.Set;

/**
 * An immutable record containing the mod's Config object.
 * It is not persistent; the record will be replaced with a new on reload.
 * <br/>
 * All error-handling, loading, and serializing are handled by the Manager.
 */
public record ModConfig(
    @JsonProperty("config_version") Integer configVersion,

    @JsonProperty("targets") List<TargetConfig> targetList,

    @JsonProperty("readout_blocks") Set<String> readoutBlockSet,

    @JsonProperty("readout_window_in_seconds") Integer readoutWindowInSeconds
) {}
