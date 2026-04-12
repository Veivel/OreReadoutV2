package com.github.Veivel.mixin;

import com.github.Veivel.notifier.DispatchBuffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.World;
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
        World world,
        BlockPos pos,
        BlockState state,
        PlayerEntity player,
        CallbackInfoReturnable<?> ci
    ) {
        Block block = state.getBlock();
        String blockName = Registries.BLOCK.getId(block)
            .toString()
            .replaceFirst("minecraft:", "");

        DispatchBuffer.append(blockName, pos, world, player);
    }
}
