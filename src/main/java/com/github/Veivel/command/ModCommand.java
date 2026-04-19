package com.github.Veivel.command;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.perms.Perms;
import com.github.Veivel.store.PlayerConfigStore;
import com.github.Veivel.util.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Map;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.Logger;

public class ModCommand {

    private static final Logger LOGGER = OreReadoutMod.LOGGER;

    private ModCommand() {}

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext registryAccess,
        CommandSelection environment
    ) {
        LiteralCommandNode<CommandSourceStack> baseNode = Commands.literal(
            "ore"
        )
            .requires(
                Permissions.require(Perms.ROOT, PermissionLevel.MODERATORS)
            )
            .build();
        LiteralCommandNode<CommandSourceStack> toggleCommandNode =
            Commands.literal("toggle")
                .requires(
                    Permissions.require(
                        Perms.TOGGLE,
                        PermissionLevel.MODERATORS
                    )
                )
                .executes(ModCommand::toggleChatReadoutsBySelf)
                .build();
        LiteralCommandNode<CommandSourceStack> reloadCommandNode =
            Commands.literal("reload")
                .requires(
                    Permissions.require(Perms.RELOAD, PermissionLevel.ADMINS)
                )
                .executes(ModCommand::reload)
                .build();

        dispatcher.getRoot().addChild(baseNode);
        baseNode.addChild(toggleCommandNode);
        baseNode.addChild(reloadCommandNode);
    }

    public static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean canReload = Permissions.check(source, Perms.RELOAD, false);
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
            ModConfigManager.load();
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
            LOGGER.error(
                "An error occurred while reloading the config, keeping old values.",
                e
            );
            return 0;
        }
    }

    public static int toggleChatReadoutsBySelf(
        CommandContext<CommandSourceStack> context
    ) {
        // command must be run by player, not from console
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            LOGGER.info("This command can only be run by a player.");
            return 0;
        }

        // check for permissions
        Player player = source.getPlayer();
        String uuid = player.getUUID().toString();
        boolean canToggle = Permissions.check(source, Perms.TOGGLE, false);
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
        Map<String, Boolean> chatReadoutEnabledByPlayer =
            PlayerConfigStore.getChatReadoutEnabledByPlayer();
        if (
            chatReadoutEnabledByPlayer.containsKey(uuid) &&
            Boolean.FALSE.equals(chatReadoutEnabledByPlayer.get(uuid))
        ) {
            chatReadoutEnabledByPlayer.put(uuid, true);
            source.sendFailure(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You will now receive ore readouts again.",
                        ChatFormatting.AQUA
                    )
                )
            );
        } else {
            chatReadoutEnabledByPlayer.put(uuid, false);
            source.sendFailure(
                TextFormat.getPrefix().append(
                    TextFormat.fmt(
                        "You will no longer receive ore readouts for this session.",
                        ChatFormatting.AQUA
                    )
                )
            );
        }
        return 1;
    }
}
