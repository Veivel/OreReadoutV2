package com.github.Veivel.notifier;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.orereadout.OreReadoutMod;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        World world,
        PlayerEntity player
    ) {
        if (map.containsKey(blockName)) {
            LOGGER.debug("Sending notification!");
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
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(playerName);
            if (player == null) {
                LOGGER.warn(
                    "Player {} does not exist or has disconnected.",
                    playerName
                );
            } else {
                World world = player.getEntityWorld();
                Dispatch.invoke(blocksMined, world, player);
            }
        });

        LOGGER.debug("Flushing playersBlocksMined map.");
        playersBlocksMined.clear();
    }
}
