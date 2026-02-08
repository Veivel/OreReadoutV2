package com.github.Veivel.notifier;

import com.github.Veivel.notifier.sink.SinkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/** Dispatches the notification to the appropriate sinks. */
public class Dispatch {

    public static void invoke(int quantity, World world, PlayerEntity player) {
        String playerName = player.getName().getString();
        String dimensionName = world
            .getRegistryKey()
            .getValue()
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
