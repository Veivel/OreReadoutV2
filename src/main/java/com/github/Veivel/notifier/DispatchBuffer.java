package com.github.Veivel.notifier;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.orereadout.OreReadoutMod;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

/**
 * Buffers events to the store using {@link #append}, then orchestrates
 * read-outs by relaying it to {@link Dispatch} using {@link #flush}.
 */
public class DispatchBuffer {

    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private static Map<String, Integer> playersBlocksMined = new HashMap<>();
    private static Map<String, Boolean> map =
        ModConfigManager.getConfig().getBlockMap();

    private DispatchBuffer() {}

    public static void append(
        String blockName,
        BlockPos pos,
        Level world,
        Player player
    ) {
        LOGGER.debug(
            "Acknowledging event for {} by {}...",
            blockName,
            player.getPlainTextName()
        );
        if (map.containsKey(blockName)) {
            LOGGER.debug("Updating playersBlocksMined map.");
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
            LOGGER.error("Could not find active MinecraftServer instance.");
            return;
        }

        playersBlocksMined.forEach((playerName, blocksMined) -> {
            PlayerList playerManager = server.getPlayerList();
            Player player = playerManager.getPlayer(playerName);
            if (player == null) {
                LOGGER.warn(
                    "Player {} does not exist or has disconnected.",
                    playerName
                );
            } else {
                Level world = player.getLivingEntity().level();
                Dispatch.invoke(blocksMined, world, player);
            }
        });

        LOGGER.debug(
            "Flushing playersBlocksMined map of size {}...",
            playersBlocksMined.size()
        );
        playersBlocksMined.clear();
    }
}
