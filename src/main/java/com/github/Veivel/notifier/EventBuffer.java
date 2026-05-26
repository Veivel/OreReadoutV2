package com.github.Veivel.notifier;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.TargetRegistry;
import com.github.Veivel.server.ServerContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

/**
 * Buffers events to the store using {@link #append}, then orchestrates
 * read-outs by calling {@link #dispatch} via {@link #flush}.
 */
public class EventBuffer {

    private final Logger logger = ModLogger.get();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private Set<String> blockMap;
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
        this.blockMap = configManager.get().readoutBlockSet();
        logger.debug("EventBuffer initialized.");
    }

    public void checkAndBuffer(
        MixinEvent mixinEvent
    ) {
        logger.debug(
            "Acknowledging event for block {} by player {}...",
            mixinEvent.blockName(),
            mixinEvent.playerName()
        );

        if (blockMap.contains(mixinEvent.blockName())) {
            logger.debug("Updating eventCountMap map.");

            String uuidString = mixinEvent.playerUuid();
            Integer currentValue = eventCountMap.get(uuidString);
            if (currentValue == null) {
                eventCountMap.put(uuidString, 1);
            } else {
                eventCountMap.put(uuidString, currentValue + 1);
            }
        }
    }

    public void flush() {
        MinecraftServer server = ServerContext.get();
        if (server == null) {
            logger.error("Could not find active MinecraftServer instance.");
            return;
        }

        eventCountMap.forEach((playerUuidString, blocksMined) -> {
            PlayerList playerManager = server.getPlayerList();
            Player player = playerManager.getPlayer(
                UUID.fromString(playerUuidString)
            );
            if (player == null) {
                logger.warn(
                    "Player {} does not exist or has disconnected.",
                    playerUuidString
                );
            } else {
                Level world = player.level();
                dispatch(blocksMined, world, player);
            }
        });

        logger.debug(
            "Flushing eventCountMap map of size {}...",
            eventCountMap.size()
        );
        eventCountMap.clear();
    }

    /** Dispatches the notification to the appropriate sinks. */
    private void dispatch(int quantity, Level world, Player player) {
        String playerName = player.getName().getString();
        String dimensionName = world
            .dimension()
            .identifier()
            .toString()
            .replaceFirst("minecraft:", "");

        ReadoutEvent event = new ReadoutEvent(
            playerName,
            quantity,
            player.getX(),
            player.getY(),
            player.getZ(),
            dimensionName
        );
        targetRegistry.emit(event);
    }
}
