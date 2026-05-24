package com.github.Veivel.orereadout;

import com.github.Veivel.command.ModCommandManager;
import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.YamlConfigManager;
import com.github.Veivel.notifier.EventBuffer;
import com.github.Veivel.notifier.target.TargetRegistry;
import com.github.Veivel.server.PreferenceManager;
import com.github.Veivel.server.ServerContext;
import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class OreReadoutMod implements ModInitializer {

    public static final String MOD_NAME = "ore-readout-v2";
    private static final int TICKS_PER_SECOND = 20;
    private static Logger logger = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        // set log level
        initLogging(Level.DEBUG);

        try {
            Path configPath = FabricLoader.getInstance()
                .getConfigDir()
                .resolve(MOD_NAME + ".yaml");
            ConfigManager configManager = new YamlConfigManager(configPath);

            PreferenceManager preferenceManager = new PreferenceManager();
            ModCommandManager commandManager = new ModCommandManager(
                configManager,
                preferenceManager
            );
            TargetRegistry targetRegistry = new TargetRegistry(
                configManager,
                preferenceManager
            );
            EventBuffer.init(configManager, targetRegistry);

            // Load config and all its consumers
            configManager.load();

            // inject server context
            ServerLifecycleEvents.SERVER_STARTED.register(ServerContext::set);
            ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                ServerContext.clear()
            );

            // register commands
            CommandRegistrationCallback.EVENT.register(
                commandManager::register
            );

            // flush notifier every few seconds, as dictated by the config
            ServerTickEvents.END_SERVER_TICK.register(
                (MinecraftServer server) -> {
                    // TODO: move listener to ServerContext?
                    int readoutWindowInSeconds = configManager
                        .get()
                        .readoutWindowInSeconds();
                    int tickDiff =
                        server.getTickCount() %
                        (TICKS_PER_SECOND * readoutWindowInSeconds);
                    if (tickDiff == 0) {
                        EventBuffer.flush();
                        return;
                    }
                }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLogging(Level logLevel) {
        Configurator.setLevel(MOD_NAME, logLevel);
        if (logger.isDebugEnabled()) {
            logger.debug("Debug logging is enabled.");
        }
    }
}
