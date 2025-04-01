package com.github.Veivel.orereadout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.DiscordWebhookSender;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OreReadout implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static DiscordWebhookSender discordWebhookSender = null;

    // map of player UUID (str) to boolean, whether they disabled ore readouts or not
    public static Map<String, Boolean> playerDisableViewMap = new HashMap<String, Boolean>();

    @Override
    public void onInitialize() {
        LOGGER.atLevel(Level.DEBUG);

        try {
            initializeConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                    .literal("ore")
                    .requires(Permissions.require("ore-readout.root", 2))
                    .build();
            LiteralCommandNode<ServerCommandSource> toggleCommandNode = CommandManager
                    .literal("toggle")
                    .requires(Permissions.require("ore-readout.toggle", 2))
                    .executes(Commands::toggle)
                    .build();

            dispatcher.getRoot().addChild(baseNode);
            baseNode.addChild(toggleCommandNode);
        });
    }

    private static void initializeConfig() throws Exception {
        ConfigManager.load();
        ModConfig config = ConfigManager.getConfig();
        discordWebhookSender = new DiscordWebhookSender(config.getDiscordWebhookUrl());

        String oreBlocksString = config.getBlockMap().keySet().toString();
        LOGGER.info("Reading out the following blocks when mined: {}", oreBlocksString);
    }
}
