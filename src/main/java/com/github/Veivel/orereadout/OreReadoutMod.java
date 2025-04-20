package com.github.Veivel.orereadout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.DiscordWebhookSender;
import com.github.Veivel.notifier.Notifier;
import com.github.Veivel.perms.Perms;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OreReadoutMod implements ModInitializer {
    public static final int TICKS_PER_SECOND = 20;
    public static final Logger LOGGER = LogManager.getLogger();
    public static DiscordWebhookSender discordWebhookSender = null;

    // map of player UUID (str) to boolean, whether they disabled ore readouts or not
    public static Map<String, Boolean> playerDisableViewMap = new HashMap<>();

    @Override
    public void onInitialize() {
        try {
            initializeConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                    .literal("ore")
                    .requires(Permissions.require(Perms.ROOT, 2))
                    .build();
            LiteralCommandNode<ServerCommandSource> toggleCommandNode = CommandManager
                    .literal("toggle")
                    .requires(Permissions.require(Perms.TOGGLE, 2))
                    .executes(Commands::toggleReadouts)
                    .build();

            dispatcher.getRoot().addChild(baseNode);
            baseNode.addChild(toggleCommandNode);
        });

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            int readoutWindowInSeconds = 7;
            int tickDiff = server.getTicks() % (TICKS_PER_SECOND * readoutWindowInSeconds);
            if (tickDiff == 0) {
                Notifier.notifyAll(server);
                return;
            } else {
                return;
            }
        });
    }

    private static void initializeConfig() throws IOException {
        ConfigManager.load();
        ModConfig config = ConfigManager.getConfig();
        discordWebhookSender = new DiscordWebhookSender(config.getDiscordWebhookUrl());
        discordWebhookSender.testWebhook();

        String oreBlocksString = config.getBlockMap().keySet().toString();
        LOGGER.info("Reading out the following blocks when mined: {}", oreBlocksString);
    }
}
