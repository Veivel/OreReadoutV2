package com.github.Veivel.orereadout.mixin;

import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.Notifier;
import com.github.Veivel.orereadout.OreReadoutMod;

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
public class MixinBlockExploded {
  private static final Logger LOGGER = OreReadoutMod.LOGGER;
  private static ModConfig config = ModConfigManager.getConfig();

  @Inject(
    method = "onExploded(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V",
    at = @At("HEAD")
  )
  public void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack,BlockPos> stackMerger, CallbackInfo ci) {
    Block block = state.getBlock();
    Map<String, Boolean> map = config.getBlockMap();
    String mapKeySet = map.keySet().toString();
    String blockName = Registries.BLOCK.getId(block).toString().replaceFirst("minecraft:", "");

    LOGGER.debug("Checking if block {} is in map of {}.", blockName, mapKeySet);

    if (map.containsKey(blockName)) {
      LivingEntity entity = explosion.getCausingEntity();
      if (entity != null && entity.isPlayer()) {
        LOGGER.debug("Sending notification!");
        Notifier.log(blockName, pos, world, (PlayerEntity) entity);
      }
    }
  }
}
