package com.github.Veivel.mixin;

import com.github.Veivel.notifier.DispatchBuffer;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockExplosionMixin {

    @Inject(
        method = "onExploded(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V",
        at = @At("HEAD")
    )
    public void onExploded(
        BlockState state,
        ServerLevel world,
        BlockPos pos,
        Explosion explosion,
        BiConsumer<ItemStack, BlockPos> stackMerger,
        CallbackInfo ci
    ) {
        String blockName = state
            .getBlock()
            .getName()
            .toString()
            .replaceFirst("minecraft:", "");

        LivingEntity entity = explosion.getIndirectSourceEntity(); // TODO: direct or indirect?
        Boolean isPlayer = entity.getType() == EntityType.PLAYER;
        if (entity != null && isPlayer) {
            DispatchBuffer.append(blockName, pos, world, (Player) entity);
        }
    }
}
