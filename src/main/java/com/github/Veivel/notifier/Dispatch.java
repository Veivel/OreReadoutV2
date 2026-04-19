package com.github.Veivel.notifier;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.sink.SinkManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Dispatches the notification to the appropriate sinks. */
public class Dispatch {

    public static void invoke(int quantity, Level world, Player player) {
        String playerName = player.getName().getString();
        String dimensionName = world
            .dimension()
            .identifier()
            .toString()
            .replaceFirst("minecraft:", "");

        ReadoutEvent event = new ReadoutEvent(
            playerName,
            quantity,
            player.getX(),
            player.getY(),
            player.getZ(),
            dimensionName
        );
        event.truncateCoordinates();
        SinkManager.emit(event);
    }
}
