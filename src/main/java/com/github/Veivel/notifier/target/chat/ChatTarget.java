package com.github.Veivel.notifier.target.chat;

import com.github.Veivel.command.ModPermission;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.Target;
import com.github.Veivel.notifier.target.TargetConfig;
import com.github.Veivel.server.PreferenceManager;
import com.github.Veivel.server.ServerContext;
import com.github.Veivel.util.TextFormat;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class ChatTarget implements Target {

    private final Logger logger = ModLogger.get();
    private PreferenceManager preferenceManager;
    private ChatConfig config;

    public ChatTarget(ChatConfig config, PreferenceManager preferenceManager) {
        this.config = config;
        this.preferenceManager = preferenceManager;
    }

    public void sendReadout(ReadoutEvent event) {
        try {
            MinecraftServer server = ServerContext.get();
            if (server == null) {
                logger.error("Could not find active MinecraftServer instance.");
                return;
            }

            MutableComponent mainText = composeText(
                event.playerName,
                event.quantity,
                event.x,
                event.y,
                event.z,
                event.dimension
            );

            // check perms for each player, send mainText if hasPermission
            server
                .getPlayerList()
                .getPlayers()
                .forEach(serverPlayerEntity -> {
                    String uuidStr = serverPlayerEntity.getUUID().toString();
                    Permissions.check(
                        serverPlayerEntity.getUUID(),
                        ModPermission.VIEW_READOUT,
                        false
                    ).thenAccept(hasPermissionBoolean -> {
                        logger.debug(
                            "Permission check passed for player {} {}.",
                            serverPlayerEntity.getName().getString(),
                            uuidStr
                        );

                        // Double check permission (not sure if redundant or not)
                        boolean hasPermission = Boolean.TRUE.equals(
                            hasPermissionBoolean
                        );
                        logger.debug("Permission check {}", hasPermission);
                        if (!hasPermission) {
                            return;
                        }

                        // Check for player's toggle settings
                        boolean hasReadoutEnabled =
                            (boolean) preferenceManager.get(
                                uuidStr,
                                "chat-readout",
                                true
                            );
                        logger.debug("Preference check {}", hasReadoutEnabled);
                        if (!hasReadoutEnabled) {
                            return;
                        }

                        try {
                            serverPlayerEntity.sendSystemMessage(mainText);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public boolean healthCheck() {
        if (config == null) {
            return false;
        } else if (preferenceManager == null) {
            return false;
        }

        MinecraftServer server = ServerContext.get();
        if (server == null) {
            return false;
        }

        return true;
    }

    private MutableComponent composeText(
        String playerName,
        int quantity,
        int x,
        int y,
        int z,
        String dimension
    ) {
        HoverEvent showText = new ShowText(
            TextFormat.fmt(
                "Click to teleport to the location.",
                ChatFormatting.GOLD
            )
        );
        ClickEvent suggestCommand = new SuggestCommand(
            String.format("/tp %d %d %d", x, y, z)
        );

        // text that includes coordinates, click event, & hover event
        Style style = Style.EMPTY.withHoverEvent(showText).withClickEvent(
            suggestCommand
        );

        MutableComponent clickableText = TextFormat.fmt(
            "[" + x + " ",
            ChatFormatting.AQUA
        )
            .append(TextFormat.fmt(y + " ", ChatFormatting.AQUA))
            .append(TextFormat.fmt(z + "]", ChatFormatting.AQUA))
            .setStyle(style);

        // main text
        MutableComponent mainText = TextFormat.getPrefix()
            .append(TextFormat.fmt(playerName, ChatFormatting.AQUA))
            .append(TextFormat.fmt(" mined ", ChatFormatting.WHITE))
            .append(
                TextFormat.fmt(quantity + " ores at ", ChatFormatting.WHITE)
            )
            .append(clickableText)
            .append(
                TextFormat.fmt(" in " + dimension + ".", ChatFormatting.WHITE)
            );

        return mainText;
    }

    @Override
    public TargetConfig getConfig() {
        return config;
    }
}
