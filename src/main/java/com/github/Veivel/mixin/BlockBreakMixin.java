package com.github.Veivel.mixin;

import com.github.Veivel.notifier.DispatchBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockBreakMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"))
    public void playerDestroy(
        Level world,
        Player player,
        BlockPos pos,
        BlockState state,
        BlockEntity blockEntity,
        ItemStack destroyWith,
        CallbackInfo ci
    ) {
        String blockName = state
            .getBlock()
            .getDescriptionId()
            .replaceFirst("block.minecraft.", "");

        DispatchBuffer.append(blockName, pos, world, player);
    }
}
