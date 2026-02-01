package com.github.Veivel.notifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.context.ServerContext;
import com.github.Veivel.orereadout.OreReadoutMod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


/**
 * Aggregates ore/block mining events per player and periodically relays
 * notifications to the Dispatcher.
 * <p>
 * Each player’s mined block count is accumulated during the readout window.
 * At the end of the window, {@link #flush()} dispatches notifications for all players
 * and then clears the record.
 * <p>
 * Uses the {@link Dispatcher} to relay batch totals to outputs.
 */
public class Notifier {
    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private static Map<String, Integer> playersBlocksMined = new HashMap<>();

    public static void store(String blockName, BlockPos pos, World world, PlayerEntity player) {
        LOGGER.debug("Sending notification!");
        String playerName = player.getName().getString();
        Integer currentValue = playersBlocksMined.get(playerName);
        if (currentValue == null) {
          playersBlocksMined.put(playerName, 1);
        } else {
          playersBlocksMined.put(playerName, currentValue + 1);
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
            LOGGER.warn("Player {} does not exist or has disconnected.", playerName);
          } else {
            World world = player.getEntityWorld();
            Dispatcher.dispatch(blocksMined, world, player);
          }
        });

        LOGGER.debug("Flushing playersBlocksMined map.");
        playersBlocksMined.clear();
    }
}
