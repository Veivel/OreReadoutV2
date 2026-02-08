package com.github.Veivel.notifier.sink;

import com.github.Veivel.context.ServerContext;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.perms.Perms;
import com.github.Veivel.util.TextFormat;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.SuggestCommand;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatSink extends AbstractSink {

  public ChatSink() {
    super();
    setLogger(OreReadoutMod.LOGGER);
  }

  public void readOut(
    String playerName,
    int quantity,
    int x,
    int y,
    int z,
    String dimension
  ) {
    try {
      MinecraftServer server = ServerContext.get();
      if (server == null) {
        getLogger().error("Could not find active MinecraftServer instance.");
        return;
      }

      Text mainText = composeText(playerName, quantity, x, y, z, dimension);

      // check perms for each player, send mainText if hasPermission
      server
        .getPlayerManager()
        .getPlayerList()
        .forEach(serverPlayerEntity -> {
          String uuidStr = serverPlayerEntity.getUuidAsString();

          Permissions
            .check(serverPlayerEntity.getUuid(), Perms.VIEW_READOUT, false)
            .thenAccept(hasPermissionBoolean -> {
              getLogger()
                .debug(
                  "Permission check passed for player {} {}.",
                  serverPlayerEntity.getName().getString(),
                  uuidStr
                );

              // check for player's toggle settings
              boolean hasPermission = Boolean.TRUE.equals(hasPermissionBoolean);
              boolean hasToggledOff = false;
              boolean hasKey = OreReadoutMod.playerDisableViewMap.containsKey(
                uuidStr
              );

              if (hasKey) hasToggledOff =
                OreReadoutMod.playerDisableViewMap.get(uuidStr);
              if (hasPermission && !hasToggledOff) {
                try {
                  serverPlayerEntity.sendMessage(mainText);
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
            });
        });
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  private Text composeText(
    String playerName,
    int quantity,
    int x,
    int y,
    int z,
    String dimension
  ) {
    HoverEvent showText = new ShowText(
      TextFormat.fmt("Click to teleport to the location.", Formatting.GOLD)
    );
    ClickEvent suggestCommand = new SuggestCommand(
      String.format("/tp %d %d %d", x, y, z)
    );

    // text that includes coordinates, click event, & hover event
    Style style = Style.EMPTY
      .withHoverEvent(showText)
      .withClickEvent(suggestCommand);

    MutableText clickableText = TextFormat
      .fmt("[" + x + " ", Formatting.AQUA)
      .append(TextFormat.fmt(y + " ", Formatting.AQUA))
      .append(TextFormat.fmt(z + "]", Formatting.AQUA))
      .setStyle(style);

    // main text
    Text mainText = TextFormat
      .getPrefix()
      .append(TextFormat.fmt(playerName, Formatting.AQUA))
      .append(TextFormat.fmt(" mined ", Formatting.WHITE))
      .append(TextFormat.fmt(quantity + " ores at ", Formatting.WHITE))
      .append(clickableText)
      .append(TextFormat.fmt(" in " + dimension + ".", Formatting.WHITE));

    return mainText;
  }
}
