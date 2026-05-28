package com.github.Veivel.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MixinEventAdapter {

    public static MixinEvent from(
        BlockState state,
        BlockPos pos,
        Level world,
        Player player
    ) {
        String blockName = state
            .getBlock()
            .getDescriptionId()
            .replaceFirst("block.minecraft.", "");

        // We use UUID over username because playerManager.getPlayer(UUID)
        // is much faster [O(1)] than playerManager.getPlayer(username) [O(n)].
        String playerUuidString = player.getStringUUID();
        String playerName = player.getPlainTextName();

        String dimension = world.dimension().identifier().toString();

        return new MixinEvent(
            playerUuidString,
            playerName,
            blockName,
            dimension,
            pos.getX(),
            pos.getY(),
            pos.getZ()
        );
    }
}
