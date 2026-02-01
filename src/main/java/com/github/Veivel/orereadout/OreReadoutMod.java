package com.github.Veivel.orereadout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.command.ModCommand;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.notifier.DispatchBuffer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class OreReadoutMod implements ModInitializer {
  public static final int TICKS_PER_SECOND = 20;
  public static final Logger LOGGER = LogManager.getLogger("orereadoutv2");

  // TODO: refactor into a Store
  // map of player UUID (str) to boolean, whether they disabled ore readouts or not
  public static Map<String, Boolean> playerDisableViewMap = new HashMap<>();

  @Override
  public void onInitialize() {
    // set log level
    Configurator.setLevel("orereadoutv2", org.apache.logging.log4j.Level.INFO);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Debug logging is enabled.");
    }

    try {
      initializeConfig();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // inject server context
    ServerLifecycleEvents.SERVER_STARTED.register(ServerContext::set);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerContext.clear());

    // register commands
    CommandRegistrationCallback.EVENT.register(ModCommand::register);

    // flush notifier every 7 seconds
    ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
      int readoutWindowInSeconds = 7; // TODO: make this configurable
      int tickDiff = server.getTicks() % (TICKS_PER_SECOND * readoutWindowInSeconds);
      if (tickDiff == 0) {
        DispatchBuffer.flush();
        return;
      }
    });
  }

  private static void initializeConfig() throws IOException {
      ModConfigManager.load();
      ModConfig config = ModConfigManager.getConfig();

      int blockMapSize = config.getBlockMap().size();
      LOGGER.info("{} blocks configured to trigger readouts.", blockMapSize);
  }
}
