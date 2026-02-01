package com.github.Veivel.orereadout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.command.ModCommand;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.Notifier;
import com.github.Veivel.notifier.sink.ChatSink;
import com.github.Veivel.notifier.sink.ConsoleSink;
import com.github.Veivel.notifier.sink.DiscordWebhookSink;
import com.github.Veivel.perms.Perms;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OreReadoutMod implements ModInitializer {
  public static final int TICKS_PER_SECOND = 20;
  public static final Logger LOGGER = LogManager.getLogger("orereadoutv2");
  public static DiscordWebhookSink discordWebhookSender;
  public static ConsoleSink consoleSink;
  public static ChatSink chatSink;

  // map of player UUID (str) to boolean, whether they disabled ore readouts or not
  public static Map<String, Boolean> playerDisableViewMap = new HashMap<>();

  @Override
  public void onInitialize() {
    try {
      initializeConfig();
    } catch (IOException e) {
      e.printStackTrace();
    }

    ServerLifecycleEvents.SERVER_STARTED.register(ServerContext::set);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerContext.clear());

    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
        .literal("ore")
        .requires(Permissions.require(Perms.ROOT, 2))
        .build();
      LiteralCommandNode<ServerCommandSource> toggleCommandNode = CommandManager
        .literal("toggle")
        .requires(Permissions.require(Perms.TOGGLE, 2))
        .executes(ModCommand::toggleReadouts)
        .build();
      LiteralCommandNode<ServerCommandSource> reloadCommandNode = CommandManager
        .literal("reload")
        .requires(Permissions.require(Perms.RELOAD, 4))
        .executes(ModCommand::reload)
        .build();

      dispatcher.getRoot().addChild(baseNode);
      baseNode.addChild(toggleCommandNode);
      baseNode.addChild(reloadCommandNode);
    });

    ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
      int readoutWindowInSeconds = 7;
      int tickDiff = server.getTicks() % (TICKS_PER_SECOND * readoutWindowInSeconds);
      if (tickDiff == 0) {
        Notifier.flush();
        return;
      } else {
        return;
      }
    });
  }

  private static void initializeConfig() throws IOException {
      ModConfigManager.load();
      ModConfig config = ModConfigManager.getConfig();

      consoleSink = new ConsoleSink();
      chatSink = new ChatSink();
      discordWebhookSender = new DiscordWebhookSink(config.getDiscordWebhookUrl());
      discordWebhookSender.testWebhook();

      int blockMapSize = config.getBlockMap().size();
      LOGGER.info("{} blocks configured to trigger readouts.", blockMapSize);
  }
}
