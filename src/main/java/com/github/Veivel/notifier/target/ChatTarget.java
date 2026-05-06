package com.github.Veivel.notifier.target;

import com.github.Veivel.command.ModPermission;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.orereadout.OreReadoutMod;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatTarget extends AbstractTarget {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private final String targetCode = "server_chat";
    private PreferenceManager preferenceManager;

    public ChatTarget(PreferenceManager preferenceManager) {
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

                        // check for player's toggle settings
                        boolean hasPermission = Boolean.TRUE.equals(
                            hasPermissionBoolean
                        );
                        logger.debug("Permission check {}", hasPermission);
                        boolean hasReadoutEnabled =
                            (boolean) preferenceManager.get(
                                uuidStr,
                                "chat-readout",
                                true
                            );
                        logger.debug("Preference check {}", hasReadoutEnabled);
                        if (hasPermission && hasReadoutEnabled) {
                            try {
                                serverPlayerEntity.sendSystemMessage(mainText);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private MutableComponent composeText(
        String playerName,
        int quantity,
        double x,
        double y,
        double z,
        String dimension
    ) {
        HoverEvent showText = new ShowText(
            TextFormat.fmt(
                "Click to teleport to the location.",
                ChatFormatting.GOLD
            )
        );
        ClickEvent suggestCommand = new SuggestCommand(
            String.format("/tp %.2f %.2f %.2f", x, y, z)
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
}
