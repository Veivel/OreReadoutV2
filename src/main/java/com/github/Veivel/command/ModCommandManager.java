package com.github.Veivel.command;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.server.PreferenceManager;
import com.github.Veivel.util.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.Logger;

public class ModCommandManager {

    private final Logger logger = ModLogger.get();
    private ConfigManager configManager;
    private PreferenceManager preferenceManager;

    public ModCommandManager(
        ConfigManager configManager,
        PreferenceManager preferenceManager
    ) {
        this.configManager = configManager;
        this.preferenceManager = preferenceManager;
    }

    public void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext registryAccess,
        CommandSelection environment
    ) {
        LiteralCommandNode<CommandSourceStack> baseNode = Commands.literal(
            "ore"
        )
            .requires(
                Permissions.require(
                    ModPermission.ROOT,
                    PermissionLevel.MODERATORS
                )
            )
            .build();
        LiteralCommandNode<CommandSourceStack> toggleCommandNode =
            Commands.literal("toggle")
                .requires(
                    Permissions.require(
                        ModPermission.TOGGLE,
                        PermissionLevel.MODERATORS
                    )
                )
                .executes(this::toggleChatReadoutsBySelf)
                .build();
        LiteralCommandNode<CommandSourceStack> reloadCommandNode =
            Commands.literal("reload")
                .requires(
                    Permissions.require(
                        ModPermission.RELOAD,
                        PermissionLevel.ADMINS
                    )
                )
                .executes(this::reload)
                .build();

        dispatcher.getRoot().addChild(baseNode);
        baseNode.addChild(toggleCommandNode);
        baseNode.addChild(reloadCommandNode);
    }

    public int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean canReload = Permissions.check(
            source,
            ModPermission.RELOAD,
            false
        );
        // check for permissions
        if (!canReload) {
            source.sendFailure(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You do not have the permissions for this.",
                        ChatFormatting.RED
                    )
                )
            );
            return 0;
        }

        // attempt to load mod config
        try {
            source.sendSystemMessage(
                // TODO: look into sendSuccess vs sendSystemMessage
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "Reloading OreReadoutV2's config...",
                        ChatFormatting.AQUA
                    )
                )
            );
            configManager.load();
            source.sendSystemMessage(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "OreReadoutV2 reloaded!",
                        ChatFormatting.AQUA
                    )
                )
            );
            return 1;
        } catch (Exception e) {
            // error occurred
            source.sendFailure(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "An error occurred while reloading the config, keeping old values.",
                        ChatFormatting.RED
                    )
                )
            );
            logger.error(
                "An error occurred while reloading the config, keeping old values.",
                e
            );
            return 0;
        }
    }

    public int toggleChatReadoutsBySelf(
        CommandContext<CommandSourceStack> context
    ) {
        // command must be run by player, not from console
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            logger.info("This command can only be run by a player.");
            return 0;
        }

        // check for permissions
        Player player = source.getPlayer();
        String uuidStr = player.getUUID().toString();
        boolean canToggle = Permissions.check(
            source,
            ModPermission.TOGGLE,
            false
        );
        if (!canToggle) {
            source.sendFailure(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You do not have the permissions for this.",
                        ChatFormatting.RED
                    )
                )
            );
            return 0;
        }

        // check current status, then toggle
        boolean hasReadoutEnabled = (boolean) preferenceManager.get(
            uuidStr,
            "chat-readout",
            true
        );
        if (hasReadoutEnabled) {
            preferenceManager.put(uuidStr, "chat-readout", false);
            source.sendSystemMessage(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You will no longer receive ore readouts for this session.",
                        ChatFormatting.AQUA
                    )
                )
            );
        } else {
            preferenceManager.put(uuidStr, "chat-readout", true);
            source.sendSystemMessage(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You will now receive ore readouts again.",
                        ChatFormatting.AQUA
                    )
                )
            );
        }
        return 1;
    }
}
