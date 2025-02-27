package com.github.Veivel.orereadout;

import java.util.HashMap;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class Commands {
    public static int toggle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String TOGGLE_PERMISSIONS = "ore-readout.toggle";
        ServerCommandSource source = context.getSource();
        if (!source.isExecutedByPlayer()) {
            OreReadout.LOG.info("This command can only be run by a player.");
            return 1;
        }

        ServerPlayerEntity player = source.getPlayer();
        String uuid = player.getUuidAsString();
        Boolean canToggle = Permissions.check(source, TOGGLE_PERMISSIONS, false);
        if (!canToggle) {
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("You do not have the permissions for this.", Formatting.AQUA))
            );
            return 1;
        }

        HashMap<String, Boolean> disableViewMap = OreReadout.playerDisableViewMap;
        if (!disableViewMap.containsKey(uuid)) {
            disableViewMap.put(uuid, true);
            source.sendMessage(
                Utils
                .oreReadoutPrefix()
                .append(Utils.fmt("Ore readouts have been disabled for this session.", Formatting.AQUA))
            );
        } else if (disableViewMap.get(uuid)) {
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
