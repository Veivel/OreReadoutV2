package com.github.Veivel.mixin;

import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.MixinEventAdapter;
import com.github.Veivel.notifier.EventBufferRelay;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
        ServerLevel world,
        ServerPlayer player,
        BlockPos pos,
        BlockState state,
        BlockEntity blockEntity,
        ItemStack destroyWith,
        CallbackInfo ci
    ) {
        MixinEvent mixinEvent = MixinEventAdapter.from(
            state,
            pos,
            world,
            player
        );
        EventBufferRelay.checkAndBuffer(mixinEvent);
    }
}
