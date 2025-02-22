package com.github.Veivel.orereadout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.Veivel.orereadout.OreReadout;
import com.github.Veivel.orereadout.Utils;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public class MixinBlockBreak {

    @Inject(method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"))
    public void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable ci) {
        Block block = state.getBlock();
        String blockName = Registries.BLOCK.getId(block).toString().replaceFirst("minecraft:", "");
        if (OreReadout.blockMap.containsKey(blockName)) {
            notify(blockName, pos, world, player);
        }
    }

    private void notify(String blockName, BlockPos pos, World world, PlayerEntity player) {
        String VIEW_LOGS_PERMISSION = "ore-readout.view";
        String playerName = player.getName().getString();
        String dimensionName = world.getRegistryKey().getValue().toString().replaceFirst("minecraft:", "");

        // send to server console
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

        // send to specified players via chat
        if (OreReadout.sendToChat) {
            try {
                // text that includes coordinates, click event, & hover event
                Style style = Style.EMPTY
                    .withHoverEvent(new HoverEvent(
                        net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                        Utils.fmt("Click here to teleport to the location.", Formatting.GOLD)
                    ))
                    .withClickEvent(new ClickEvent(
                        Action.SUGGEST_COMMAND, 
                        String.format("/tp @s %d %d %d", pos.getX(), pos.getY(), pos.getZ())
                    ));
                MutableText clickableText = Utils
                    .fmt("[" + pos.getX() + " ", Formatting.AQUA)
                    .append(Utils.fmt(pos.getY() + " ", Formatting.AQUA))
                    .append(Utils.fmt(pos.getZ() + "]", Formatting.AQUA))
                    .setStyle(style);

                // main text
                Text mainText = Text.of("🔔").copy().formatted(Formatting.AQUA)
                    .append(Utils.fmt(" » ", Formatting.GRAY))
                    .append(Utils.fmt(playerName, Formatting.AQUA))
                    .append(Utils.fmt(" mined ", Formatting.WHITE))
                    .append(Utils.fmt(blockName + " at ", Formatting.WHITE))
                    .append(clickableText)
                    .append(Utils.fmt(" in " + dimensionName + ".", Formatting.WHITE));

                // check perms for each player, send mainText if hasPermission
                player.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                    Permissions
                    .check(serverPlayerEntity.getUuid(), VIEW_LOGS_PERMISSION, false)
                    .thenAcceptAsync(hasPermission -> {
                        if (hasPermission) {
                            try {
                                serverPlayerEntity.sendMessage(mainText);
                            } catch(Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                });
            } catch(Exception e1) {
                e1.printStackTrace();
            }

        }

        // send to discord webhook
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
