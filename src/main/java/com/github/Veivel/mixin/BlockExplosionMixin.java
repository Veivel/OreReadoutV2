package com.github.Veivel.mixin;

import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.MixinEventAdapter;
import com.github.Veivel.notifier.EventBufferRelay;

import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockExplosionMixin {

    @Inject(method = "onExplosionHit", at = @At("HEAD"))
    public void onExplosionHit(
        BlockState state,
        ServerLevel world,
        BlockPos pos,
        Explosion explosion,
        BiConsumer<ItemStack, BlockPos> stackMerger,
        CallbackInfo ci
    ) {
        // Mimics the same check in BlockBehaviour.onExplosionHit()
        if (
            !state.isAir() &&
            explosion.getBlockInteraction() != BlockInteraction.TRIGGER_BLOCK
        ) {
            LivingEntity entity = explosion.getIndirectSourceEntity(); // Indirect source finds the source at the root of the explosion chain
            if (entity != null && entity instanceof Player) {
                MixinEvent mixinEvent = MixinEventAdapter.from(state, pos, world, (Player) entity);
                EventBufferRelay.checkAndBuffer(mixinEvent);
            }
        }
    }
}
