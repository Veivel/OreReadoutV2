package com.github.Veivel.orereadout;

import com.github.Veivel.command.ModCommand;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.context.ServerContext;
import com.github.Veivel.notifier.DispatchBuffer;
import com.github.Veivel.notifier.sink.SinkManager;
import java.io.IOException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class OreReadoutMod implements ModInitializer {

    public static final int TICKS_PER_SECOND = 20;
    public static final Logger LOGGER = LogManager.getLogger("orereadoutv2");

    @Override
    public void onInitialize() {
        // set log level
        Configurator.setLevel(
            "orereadoutv2",
            org.apache.logging.log4j.Level.DEBUG
        );
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
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
            ServerContext.clear()
        );

        // register commands
        CommandRegistrationCallback.EVENT.register(ModCommand::register);

        // flush notifier every 7 seconds
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            ModConfig config = ModConfigManager.getConfig();
            int readoutWindowInSeconds = config.getReadoutWindowInSeconds();
            int tickDiff =
                server.getTickCount() %
                (TICKS_PER_SECOND * readoutWindowInSeconds); // TODO: test if getTickCount is logically correct or not
            if (tickDiff == 0) {
                DispatchBuffer.flush();
                return;
            }
        });
    }

    private static void initializeConfig() throws IOException {
        ModConfigManager.load();
        ModConfig config = ModConfigManager.getConfig();

        SinkManager.init(config);

        int blockMapSize = config.getBlockMap().size();
        LOGGER.info("{} blocks configured to trigger readouts.", blockMapSize);
    }
}
