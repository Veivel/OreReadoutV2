package com.github.Veivel.config;

import com.github.Veivel.notifier.target.TargetConfig;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
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

    @Setting("blocks_broken_threshold") @NonNull Integer blocksBrokenThreshold,

    @Setting("debug_mode") boolean debugMode
) {
    public void validate() throws NoSuchElementException {
        // NOTE: There's probably a cleaner way to do this, but it works
        if (configVersion() == null) {
            throw new NoSuchElementException(
                "configVersion is missing from the configuration"
            );
        } else if (readoutWindowInSeconds() == null) {
            throw new NoSuchElementException(
                "readoutWindowInSeconds is missing from the configuration"
            );
        } else if (blocksBrokenThreshold() == null) {
            throw new NoSuchElementException(
                "blocksBrokenThreshold is missing from the configuration"
            );
        } else if (targets() == null) {
            throw new NoSuchElementException(
                "targets is missing from the configuration"
            );
        } else if (readoutBlockSet() == null) {
            throw new NoSuchElementException(
                "readoutBlockSet is missing from the configuration"
            );
        }
    }
}
