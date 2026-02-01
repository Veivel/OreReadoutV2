package com.github.Veivel.notifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.sink.ChatSink;
import com.github.Veivel.orereadout.OreReadoutMod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Notifier {
    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private static ModConfig config = ModConfigManager.getConfig();
    private static Map<String, Integer> playersBlocksMined = new HashMap<>();
    private static final ChatSink CHAT_SINK = new ChatSink();

    public static void log(String blockName, BlockPos pos, World world, PlayerEntity player) {
        String playerName = player.getName().getString();
        Integer currentValue = playersBlocksMined.get(playerName);
        if (currentValue == null) {
          playersBlocksMined.put(playerName, 1);
        } else {
          playersBlocksMined.put(playerName, currentValue + 1);
        }
    }

    public static void flushReadouts() {
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
            notify(blocksMined, world, player);
          }
        });
        playersBlocksMined.clear();
    }

    public static void notify(int quantity, World world, PlayerEntity player) {
        String playerName = player.getName().getString();
        String dimensionName = world.getRegistryKey().getValue().toString().replaceFirst("minecraft:", "");

        // send to server console
        if (config.isSendToConsole()) {
          OreReadoutMod.consoleSink.readOut(
            playerName,
            quantity,player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ(),
            dimensionName
          );
        }

        // send to specified players via in-game chat
        if (config.isSendToIngame()) {
          CHAT_SINK.readOut(
            playerName,
            quantity,
            player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ(),
            dimensionName
          );
        }

        // send to discord webhook
        if (config.isSendToDiscord()) {
            OreReadoutMod.discordWebhookSender.readOut(
              playerName,
              quantity,
              player.getBlockX(),
              player.getBlockY(),
              player.getBlockZ(),
              dimensionName
            );
        }
    }
}
