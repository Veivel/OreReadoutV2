package com.github.Veivel.command;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.perms.Perms;
import com.github.Veivel.util.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class ModCommand {
    private static final Logger logger = OreReadoutMod.LOGGER;

    private ModCommand() {};

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
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
    }

    public static int reload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean canReload = Permissions.check(source, Perms.RELOAD, false);
        // check for permissions
        if (!canReload) {
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("You do not have the permissions for this.", Formatting.RED))
            );
            return 0;
        }

        // attempt to load mod config
        try {
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("Reloading OreReadoutV2's config...", Formatting.AQUA))
            );
            ModConfigManager.load();
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("OreReadoutV2 reloaded!", Formatting.AQUA))
            );
            return 1;
        } catch (Exception e) {
            // error occurred
            source.sendError(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("An error occurred while reloading the config, keeping old values.", Formatting.RED))
            );
            LOGGER.error("An error occurred while reloading the config, keeping old values.", e);
            return 0;
        }
    }

    public static int toggleReadouts(CommandContext<ServerCommandSource> context) {
        // command must be run by player, not from console
        ServerCommandSource source = context.getSource();
        if (!source.isExecutedByPlayer()) {
            LOGGER.info("This command can only be run by a player.");
            return 0;
        }

        // check for permissions
        ServerPlayerEntity player = source.getPlayer();
        String uuid = player.getUuidAsString();
        boolean canToggle = Permissions.check(source, Perms.TOGGLE, false);
        if (!canToggle) {
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("You do not have the permissions for this.", Formatting.RED))
            );
            return 0;
        }

        // check current status, then toggle
        Map<String, Boolean> disableViewMap = OreReadoutMod.playerDisableViewMap;
        if (disableViewMap.containsKey(uuid) && Boolean.TRUE.equals(disableViewMap.get(uuid))) {
            disableViewMap.put(uuid, false);
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("You will now receive ore readouts again.", Formatting.AQUA))
            );
        } else {
            disableViewMap.put(uuid, true);
            source.sendMessage(
                TextFormat
                .getPrefix()
                .append(TextFormat.fmt("You will no longer receive ore readouts for this session.", Formatting.AQUA))
            );
        }
        return 1;
    }
}
