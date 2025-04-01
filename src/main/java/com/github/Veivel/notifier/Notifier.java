package com.github.Veivel.notifier;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.orereadout.Utils;
import com.github.Veivel.perms.Perms;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.SuggestCommand;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Notifier {
  private static final Logger LOGGER = OreReadoutMod.LOGGER;
  private static ModConfig config = ConfigManager.getConfig();

  private Notifier() {}

  // TODO: refactor to use Strategy or Interface for each readout destination
  public static void notify(String blockName, BlockPos pos, World world, PlayerEntity player) {
    String playerName = player.getName().getString();
    // TODO: check for bypass

    String dimensionName = world.getRegistryKey().getValue().toString().replaceFirst("minecraft:", "");

    // send to server console
    if (config.isSendToConsole()) {
        LOGGER.info(
            "{} mined {} at [{} {} {}] in {}",
            playerName, blockName, pos.getX(), pos.getY(), pos.getZ(), dimensionName
        );
    }

    // send to specified players via chat
    if (config.isSendToIngame()) {
        try {
            HoverEvent showText = new ShowText(
                Utils.fmt("Click here to teleport to the location.", Formatting.GOLD)
            );
            ClickEvent suggestCommand = new SuggestCommand(
                String.format("/tp %d %d %d", pos.getX(), pos.getY(), pos.getZ())
            );
            // text that includes coordinates, click event, & hover event
            Style style = Style.EMPTY
                .withHoverEvent(showText)
                .withClickEvent(suggestCommand);
            MutableText clickableText = Utils
                .fmt("[" + pos.getX() + " ", Formatting.AQUA)
                .append(Utils.fmt(pos.getY() + " ", Formatting.AQUA))
                .append(Utils.fmt(pos.getZ() + "]", Formatting.AQUA))
                .setStyle(style);

            // main text
            Text mainText = Utils
                .oreReadoutPrefix()
                .append(Utils.fmt(playerName, Formatting.AQUA))
                .append(Utils.fmt(" mined ", Formatting.WHITE))
                .append(Utils.fmt(blockName + " at ", Formatting.WHITE))
                .append(clickableText)
                .append(Utils.fmt(" in " + dimensionName + ".", Formatting.WHITE));

            // check perms for each player, send mainText if hasPermission
            player.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                String uuidStr = serverPlayerEntity.getUuidAsString();
                
                Permissions
                .check(serverPlayerEntity.getUuid(), Perms.VIEW_READOUT, false)
                .thenAcceptAsync(hasPermissionBoolean -> {
                    // check for player's toggle settings
                    boolean hasPermission = Boolean.TRUE.equals(hasPermissionBoolean);
                    boolean hasToggledOff = false;
                    boolean hasKey = OreReadoutMod.playerDisableViewMap.containsKey(uuidStr);

                    if (hasKey) hasToggledOff = OreReadoutMod.playerDisableViewMap.get(uuidStr);
                    if (hasPermission && !hasToggledOff) {
                        try {
                            serverPlayerEntity.sendMessage(mainText);
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            });
        } catch(Exception e1) {
            e1.printStackTrace();
        }

    }

    // send to discord webhook
    if (config.isSendToDiscord()) {
        OreReadoutMod.discordWebhookSender.sendReadout(
            playerName, 
            blockName, 
            pos.getX(), 
            pos.getY(), 
            pos.getZ(), 
            dimensionName
        );
    }
}
}
