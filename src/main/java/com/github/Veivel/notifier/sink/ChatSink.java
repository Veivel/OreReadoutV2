package com.github.Veivel.notifier.sink;

import com.github.Veivel.context.ServerContext;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.perms.Perms;
import com.github.Veivel.store.PlayerConfigStore;
import com.github.Veivel.util.TextFormat;
import java.util.Map;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;

public class ChatSink extends AbstractSink {

    public ChatSink() {
        super();
        setLogger(OreReadoutMod.LOGGER);
    }

    public void readOut(
        String playerName,
        int quantity,
        int x,
        int y,
        int z,
        String dimension
    ) {
        try {
            MinecraftServer server = ServerContext.get();
            if (server == null) {
                getLogger().error(
                    "Could not find active MinecraftServer instance."
                );
                return;
            }

            MutableComponent mainText = composeText(
                playerName,
                quantity,
                x,
                y,
                z,
                dimension
            );

            // check perms for each player, send mainText if hasPermission
            server
                .getPlayerList()
                .getPlayers()
                .forEach(serverPlayerEntity -> {
                    String uuidStr = serverPlayerEntity.getUUID().toString();

                    Permissions.check(
                        serverPlayerEntity.getUUID(),
                        Perms.VIEW_READOUT,
                        false
                    ).thenAccept(hasPermissionBoolean -> {
                        getLogger().debug(
                            "Permission check passed for player {} {}.",
                            serverPlayerEntity.getName().getString(),
                            uuidStr
                        );

                        // check for player's toggle settings
                        boolean hasPermission = Boolean.TRUE.equals(
                            hasPermissionBoolean
                        );
                        boolean hasChatReadoutEnabled = true;
                        Map<String, Boolean> chatReadoutEnabledByPlayer =
                            PlayerConfigStore.getChatReadoutEnabledByPlayer();
                        boolean hasKey = chatReadoutEnabledByPlayer.containsKey(
                            uuidStr
                        );

                        if (hasKey) hasChatReadoutEnabled =
                            chatReadoutEnabledByPlayer.get(uuidStr);
                        if (hasPermission && hasChatReadoutEnabled) {
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
}
