package com.github.yitzy299.orereadout.mixin;

import com.github.yitzy299.orereadout.OreReadout;

import me.lucko.fabric.api.permissions.v0.Permissions;
import main.java.com.github.yitzy299.orereadout.DiscordWebhookSender;
import main.java.com.github.yitzy299.orereadout.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
        String VIEW_LOGS_PERMISSION = "ore-readout.view";
        String playerName = player.getName().getString();
        String blockName = Registries.BLOCK.getId(block).toString().replaceFirst("minecraft:", "");
        String dimensionName = world.getRegistryKey().getValue().toString().replaceFirst("minecraft:", "");

        if (OreReadout.sendInConsole) {
            OreReadout.LOG.info(
                playerName + " mined " 
                + blockName + " at [" 
                + pos.getX() + " " 
                + pos.getY() + " " 
                + pos.getZ() + "] in " 
                + dimensionName
            );
        }
        if (OreReadout.sendToChat) {
            player.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                Permissions
                .check(serverPlayerEntity.getUuid(), VIEW_LOGS_PERMISSION, false)
                .thenAcceptAsync(hasPermission -> {
                    if (hasPermission) {
                        try {
                            serverPlayerEntity.sendMessage(
                                Text.of("🔔").copy().formatted(Formatting.AQUA)
                                .append(Utils.fmt(" » ", Formatting.GRAY))
                                .append(Utils.fmt(playerName, Formatting.AQUA))
                                .append(Utils.fmt(" mined ", Formatting.WHITE))
                                .append(Utils.fmt(blockName + " at ", Formatting.WHITE))
                                // TODO: add click action to teleport to pos
                                .append(Utils.fmt("[" + pos.getX() + " ", Formatting.AQUA))
                                .append(Utils.fmt(pos.getY() + " ", Formatting.AQUA))
                                .append(Utils.fmt(pos.getZ() + "] ", Formatting.AQUA))
                                .append(Utils.fmt("in " + dimensionName + ".", Formatting.WHITE))
                            );
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            });
        }
        if (OreReadout.sendToDiscord) {
            OreReadout.discordWebhookSender.sendOreReadout(
                playerName, 
                blockName, 
                pos.getX(), 
                pos.getY(), 
                pos.getZ(), 
                dimensionName
            );
        }
    }
}
