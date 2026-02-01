package com.github.Veivel.notifier;

import com.github.Veivel.config.ModConfig;
import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.orereadout.OreReadoutMod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Dispatcher {
  private static ModConfig config = ModConfigManager.getConfig();

  public static void dispatch(int quantity, World world, PlayerEntity player) {
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
      OreReadoutMod.chatSink.readOut(
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
