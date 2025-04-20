package com.github.Veivel.notifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.orereadout.Utils;
import com.github.Veivel.perms.Perms;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;

// 1.21.4
// import net.minecraft.text.ClickEvent.Action;

// 1.21.5
import net.minecraft.text.ClickEvent.SuggestCommand;
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
  private static Map<String, Integer> playersBlocksMined = new HashMap<>();

    private Notifier() {}

    public static void log(String blockName, BlockPos pos, World world, PlayerEntity player) {
        String playerName = player.getName().getString();
        Integer currentValue = playersBlocksMined.get(playerName);
        if (currentValue == null) {
            playersBlocksMined.put(playerName, 1);
        } else {
            playersBlocksMined.put(playerName, currentValue + 1);
        }
    }

    public static void notifyAll(MinecraftServer server) {
        playersBlocksMined.forEach((playerName, blocksMined) -> {
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(playerName);
            World world = player.getWorld();
            notify(blocksMined, world, player);
        });
        playersBlocksMined.clear();
    }

    public static void notify(int quantity, World world, PlayerEntity player) {
        String playerName = player.getName().getString();
        String dimensionName = world.getRegistryKey().getValue().toString().replaceFirst("minecraft:", "");

        // send to server console
        if (config.isSendToConsole()) {
            LOGGER.info(
                "{} mined {} ores at [{} {} {}] in {}",
                playerName, quantity, player.getBlockX(), player.getBlockY(), player.getBlockZ(), dimensionName
            );
        }

        // send to specified players via chat
        if (config.isSendToIngame()) {
            try {
                /** 1.21.5 */
                HoverEvent showText = new ShowText(
                    Utils.fmt("Click here to teleport to the player's logged location.", Formatting.GOLD)
                );
                ClickEvent suggestCommand = new SuggestCommand(
                    String.format("/tp %d %d %d", player.getBlockX(), player.getBlockY(), player.getBlockZ())
                );
                // text that includes coordinates, click event, & hover event
                Style style = Style.EMPTY
                    .withHoverEvent(showText)
                    .withClickEvent(suggestCommand);
                
                /** 1.21.4 (old fabric API) */
                // Style style = Style.EMPTY
                //     .withHoverEvent(new HoverEvent(
                //         net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                //         Utils.fmt("Click here to teleport to the location.", Formatting.GOLD)
                //     ))
                //     .withClickEvent(new ClickEvent(
                //         Action.SUGGEST_COMMAND, 
                //         String.format("/tp %d %d %d", player.getBlockX(), player.getBlockY(), player.getBlockZ())
                //     ));

                MutableText clickableText = Utils
                    .fmt("[" + player.getBlockX() + " ", Formatting.AQUA)
                    .append(Utils.fmt(player.getBlockY() + " ", Formatting.AQUA))
                    .append(Utils.fmt(player.getBlockZ() + "]", Formatting.AQUA))
                    .setStyle(style);

                // main text
                Text mainText = Utils
                    .oreReadoutPrefix()
                    .append(Utils.fmt(playerName, Formatting.AQUA))
                    .append(Utils.fmt(" mined ", Formatting.WHITE))
                    .append(Utils.fmt(quantity + " ores at ", Formatting.WHITE))
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
            OreReadoutMod.discordWebhookSender.readOut(
                playerName, 
                quantity,
                player.getBlockX(), 
                player.getBlockY(), 
                player.getBlockZ(), 
                dimensionName
            );
        }
    }
}
