package com.github.Veivel.orereadout.mixin;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.Notifier;
import com.github.Veivel.orereadout.OreReadoutMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public class MixinBlockBreak {
  private static final Logger LOGGER = OreReadoutMod.LOGGER;
  private static ModConfig config = ModConfigManager.getConfig();

  @Inject(
    method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;",
    at = @At("HEAD")
  )
  public void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<?> ci) {
    Block block = state.getBlock();
    Map<String, Boolean> map = config.getBlockMap();
    String mapKeySet = map.keySet().toString();
    String blockName = Registries.BLOCK.getId(block).toString().replaceFirst("minecraft:", "");

    LOGGER.debug("Checking if block {} is in map of {}.", blockName, mapKeySet);

    if (map.containsKey(blockName)) {
      LOGGER.debug("Sending notification!");
      Notifier.log(blockName, pos, world, player);
    }
  }
}
