package com.github.Veivel.mixin;

import com.github.Veivel.notifier.DispatchBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockBreakMixin {

    @Inject(
        method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;",
        at = @At("HEAD")
    )
    public void onBroken(
        Level world,
        BlockPos pos,
        BlockState state,
        Player player,
        CallbackInfoReturnable<?> ci
    ) {
        String blockName = state
            .getBlock()
            .getName()
            .toString()
            .replaceFirst("minecraft:", "");

        DispatchBuffer.append(blockName, pos, world, player);
    }
}
