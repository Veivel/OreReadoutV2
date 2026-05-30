package com.github.Veivel.notifier;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.MixinEventAggregate;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.TargetRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;

/**
 * Buffers events to the store using {@link #checkAndBuffer}, then orchestrates
 * read-outs by calling {@link #dispatch} via {@link #flush}.
 */
public class EventBuffer {

    private final Logger logger = ModLogger.get();
    private Map<String, MixinEventAggregate> mixinEventAggMap = new HashMap<
        String,
        MixinEventAggregate
    >();
    private Set<String> blockSet;
    private Integer blocksBrokenThreshold;
    private ConfigManager configManager;
    private TargetRegistry targetRegistry;

    public EventBuffer(
        ConfigManager configManager,
        TargetRegistry targetRegistry
    ) {
        this.configManager = configManager;
        this.targetRegistry = targetRegistry;
        configManager.onAfterReload(() -> {
            load();
        });
    }

    private void load() {
        this.blockSet = configManager.get().readoutBlockSet();
        this.blocksBrokenThreshold = configManager
            .get()
            .blocksBrokenThreshold();
        logger.debug("EventBuffer loaded.");
    }

    public void checkAndBuffer(MixinEvent mixinEvent) {
        logger.debug(
            "Acknowledging event for block {} by player {}...",
            mixinEvent.blockName(),
            mixinEvent.playerName()
        );

        if (blockSet.contains(mixinEvent.blockName())) {
            logger.debug("Updating buffer store...");

            String uuidString = mixinEvent.playerUuid();
            MixinEventAggregate mixinEventAgg = mixinEventAggMap.get(
                uuidString
            );

            // Buffer event into store
            if (mixinEventAgg == null) {
                // Player is not present in buffer store
                mixinEventAgg = MixinEventAggregate.of(mixinEvent);
            } else {
                // Player is already present in buffer store
                mixinEventAgg = mixinEventAgg.aggregate(mixinEvent);
            }
            mixinEventAggMap.put(uuidString, mixinEventAgg);
        }
    }

    public void flush() {
        mixinEventAggMap.forEach((playerUuidString, mixinEventAgg) -> {
            dispatch(mixinEventAgg);
        });

        logger.debug(
            "Flushing buffer store of size {}...",
            mixinEventAggMap.size()
        );
        mixinEventAggMap.clear();
    }

    /**
     * Dispatches a read-out to all registered targets
     * provided that the read-out's block quantity is above the threshold.
     */
    private void dispatch(MixinEventAggregate mixinEventAgg) {
        if (mixinEventAgg.quantity() < blocksBrokenThreshold) {
            return;
        }

        ReadoutEvent event = new ReadoutEvent(
            mixinEventAgg.playerName(),
            mixinEventAgg.quantity(),
            mixinEventAgg.x(),
            mixinEventAgg.y(),
            mixinEventAgg.z(),
            mixinEventAgg.dimensionName()
        );
        targetRegistry.emit(event);
    }
}
