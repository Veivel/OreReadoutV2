package com.github.yitzy299.orereadout.mixin;

import com.github.yitzy299.orereadout.OreReadout;

import main.java.com.github.yitzy299.orereadout.DiscordWebhookSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlockBreak {
    @Inject(method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"))
    public void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable ci) {
        Block block = state.getBlock();
        if (OreReadout.blocks.contains(Registries.BLOCK.getId(block).toString())) {
            notify(block, pos, world, player);
        }
    }

    private void notify(Block block, BlockPos pos, World world, PlayerEntity player) {
        if (OreReadout.sendToDiscord) {
            DiscordWebhookSender.sendWebhook(
                player.getName().getString(), 
                Registries.BLOCK.getId(block).toString(), 
                pos.getX(), 
                pos.getY(), 
                pos.getZ(), 
                world.getRegistryKey().getValue().toString(),
                OreReadout.discordWebhookUrl
            );
        }
        if (OreReadout.sendToChat) {
            player.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                if (serverPlayerEntity.hasPermissionLevel(2)) {
                        try {
                            serverPlayerEntity.sendMessage(new Literal(Registries.BLOCK.getId(block) + " was broken by " + player.getName().getString() + " at " + pos.getX() +
                                ", " + pos.getY() + ", " + pos.getZ() + " in " + world.getRegistryKey().getValue()).parse(null, null, 0));
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
        }
        if (OreReadout.sendInConsole) {
            OreReadout.LOG.info(Registries.BLOCK.getId(block) + " was broken by " + player.getName().toString() + " at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " in " + world.getRegistryKey().getValue());
        }


    }


}
