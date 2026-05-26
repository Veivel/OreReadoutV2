package com.github.Veivel.notifier;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.TargetRegistry;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.server.ServerContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.Logger;

/**
 * Buffers events to the store using {@link #append}, then orchestrates
 * read-outs by relaying it to private method {@link #dispatch}, via {@link #flush}
 *
 * Static "global" class.
 */
public class EventBuffer {

    private static final Logger logger = ModLogger.get();
    private static Map<String, Integer> eventCountMap = new HashMap<>();
    private static volatile Set<String> blockMap;
    private static volatile ConfigManager configManager;
    private static volatile TargetRegistry targetRegistry;

    private static void load() {
        EventBuffer.blockMap = configManager.get().readoutBlockSet();
        logger.debug("EventBuffer initialized.");
    }

    public static void init(
        ConfigManager configManager,
        TargetRegistry targetRegistry
    ) {
        EventBuffer.configManager = configManager;
        EventBuffer.targetRegistry = targetRegistry;
        configManager.onAfterReload(() -> {
            load();
        });
    }

    public static void append(
        BlockState state,
        BlockPos pos,
        Level world,
        Player player
    ) {
        String blockName = state
            .getBlock()
            .getDescriptionId()
            .replaceFirst("block.minecraft.", "");

        logger.debug(
            "Acknowledging event for {} by {}...",
            blockName,
            player.getPlainTextName()
        );
        if (blockMap.contains(blockName)) {
            logger.debug("Updating eventCountMap map.");

            // We use UUID over username because playerManager.getPlayer(UUID)
            // is much faster [O(1)] than playerManager.getPlayer(username) [O(n)].
            String uuidString = player.getStringUUID();
            Integer currentValue = eventCountMap.get(uuidString);
            if (currentValue == null) {
                eventCountMap.put(uuidString, 1);
            } else {
                eventCountMap.put(uuidString, currentValue + 1);
            }
        }
    }

    public static void flush() {
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
    private static void dispatch(int quantity, Level world, Player player) {
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
