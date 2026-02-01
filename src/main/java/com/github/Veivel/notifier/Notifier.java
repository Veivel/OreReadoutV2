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

public class Notifier {
    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private static Map<String, Integer> playersBlocksMined = new HashMap<>();

    public static void log(String blockName, BlockPos pos, World world, PlayerEntity player) {
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
        playersBlocksMined.clear();
    }
}
