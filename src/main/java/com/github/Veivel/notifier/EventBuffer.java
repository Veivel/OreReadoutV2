package com.github.Veivel.notifier;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.TargetRegistry;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.server.ServerContext;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Buffers events to the store using {@link #append}, then orchestrates
 * read-outs by relaying it to private method {@link #dispatch}, via {@link #flush}.
 */
public class EventBuffer {

    private static final Logger logger = LogManager.getLogger(
        OreReadoutMod.MOD_NAME
    );
    private static Map<String, Integer> playersBlocksMined = new HashMap<>();
    private static volatile TargetRegistry registry;
    private static volatile Map<String, Boolean> map;

    public static void init(
        ModConfigManager configManager,
        TargetRegistry targetRegistry
    ) {
        map = configManager.getConfig().getBlockMap();
        registry = targetRegistry;
        logger.debug("EventBuffer initialized.");
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
        if (map.containsKey(blockName)) {
            logger.debug("Updating playersBlocksMined map.");
            String playerName = player.getName().getString();
            Integer currentValue = playersBlocksMined.get(playerName);
            if (currentValue == null) {
                playersBlocksMined.put(playerName, 1);
            } else {
                playersBlocksMined.put(playerName, currentValue + 1);
            }
        }
    }

    public static void flush() {
        MinecraftServer server = ServerContext.get();
        if (server == null) {
            logger.error("Could not find active MinecraftServer instance.");
            return;
        }

        playersBlocksMined.forEach((playerName, blocksMined) -> {
            PlayerList playerManager = server.getPlayerList();
            Player player = playerManager.getPlayer(playerName);
            if (player == null) {
                logger.warn(
                    "Player {} does not exist or has disconnected.",
                    playerName
                );
            } else {
                Level world = player.getLivingEntity().level();
                dispatch(blocksMined, world, player);
            }
        });

        logger.debug(
            "Flushing playersBlocksMined map of size {}...",
            playersBlocksMined.size()
        );
        playersBlocksMined.clear();
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
        event.truncateCoordinates();
        registry.emit(event);
    }
}
