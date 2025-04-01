package com.github.Veivel.orereadout;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.context.CommandContext;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class Commands {
    private static final Logger LOGGER = OreReadout.LOGGER;
    static String togglePermissions = "ore-readout.toggle";
    
    private Commands() {}

    public static int toggle(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!source.isExecutedByPlayer()) {
            LOGGER.info("This command can only be run by a player.");
            return 1;
        }

        ServerPlayerEntity player = source.getPlayer();
        String uuid = player.getUuidAsString();
        boolean canToggle = Permissions.check(source, togglePermissions, false);
        if (!canToggle) {
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("You do not have the permissions for this.", Formatting.AQUA))
            );
            return 1;
        }

        Map<String, Boolean> disableViewMap = OreReadout.playerDisableViewMap;
        if (disableViewMap.containsKey(uuid) && disableViewMap.get(uuid)) {
            disableViewMap.put(uuid, false);
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("Ore readouts have been re-enabled.", Formatting.AQUA))
            );
        } else {
            disableViewMap.put(uuid, true);
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("Ore readouts have been disabled for this session.", Formatting.AQUA))
            );
        }
        return 1;
    }
}
