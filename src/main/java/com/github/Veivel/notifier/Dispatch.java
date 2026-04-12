package com.github.Veivel.notifier;

import com.github.Veivel.notifier.sink.SinkManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Dispatches the notification to the appropriate sinks. */
public class Dispatch {

    public static void invoke(int quantity, Level world, Player player) {
        String playerName = player.getName().getString();
        String dimensionName = world.toString() // TODO: test if this outputs the correct string or not (Level == World?)
            .toString()
            .replaceFirst("minecraft:", "");

        SinkManager.emit(
            // TODO: create DTO
            playerName,
            quantity,
            player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ(),
            dimensionName
        );
    }
}
