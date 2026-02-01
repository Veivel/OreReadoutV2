package com.github.Veivel.mixin;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.Veivel.notifier.DispatchBuffer;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

@Mixin(AbstractBlock.class)
public class BlockExplosionMixin {
  @Inject(
    method = "onExploded(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V",
    at = @At("HEAD")
  )
  public void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack,BlockPos> stackMerger, CallbackInfo ci) {
    Block block = state.getBlock();
    String blockName = Registries.BLOCK.getId(block).toString().replaceFirst("minecraft:", "");

    LivingEntity entity = explosion.getCausingEntity();
    if (entity != null && entity.isPlayer()) {
      DispatchBuffer.append(blockName, pos, world, (PlayerEntity) entity);
    }
  }
}
