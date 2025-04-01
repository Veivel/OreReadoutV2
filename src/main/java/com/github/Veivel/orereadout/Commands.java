package com.github.Veivel.orereadout;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.perms.Perms;
import com.mojang.brigadier.context.CommandContext;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class Commands {
    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    
    private Commands() {}

    public static int toggleReadouts(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!source.isExecutedByPlayer()) {
            LOGGER.info("This command can only be run by a player.");
            return 1;
        }

        ServerPlayerEntity player = source.getPlayer();
        String uuid = player.getUuidAsString();
        boolean canToggle = Permissions.check(source, Perms.TOGGLE, false);
        if (!canToggle) {
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("You do not have the permissions for this.", Formatting.AQUA))
            );
            return 1;
        }

        Map<String, Boolean> disableViewMap = OreReadoutMod.playerDisableViewMap;
        if (disableViewMap.containsKey(uuid) && Boolean.TRUE.equals(disableViewMap.get(uuid))) {
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
