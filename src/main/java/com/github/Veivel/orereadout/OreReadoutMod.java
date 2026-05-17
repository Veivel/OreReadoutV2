package com.github.Veivel.orereadout;

import com.github.Veivel.command.ModCommandManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.config.ModConfigManager;
import com.github.Veivel.notifier.EventBuffer;
import com.github.Veivel.notifier.target.ChatTarget;
import com.github.Veivel.notifier.target.DiscordTarget;
import com.github.Veivel.notifier.target.ServerConsoleTarget;
import com.github.Veivel.notifier.target.TargetRegistry;
import com.github.Veivel.server.PreferenceManager;
import com.github.Veivel.server.ServerContext;
import java.io.IOException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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

        ModConfigManager configManager = new ModConfigManager();
        try {
            this.initConfig(configManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PreferenceManager preferenceManager = new PreferenceManager();
        ModCommandManager commandManager = new ModCommandManager(
            configManager,
            preferenceManager
        );
        ModConfig config = configManager.getConfig();
        TargetRegistry targetRegistry = new TargetRegistry();
        EventBuffer.init(config, targetRegistry);
        this.initTargets(targetRegistry, config, preferenceManager);

        // inject server context
        ServerLifecycleEvents.SERVER_STARTED.register(ServerContext::set);
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
            ServerContext.clear()
        );

        // register commands
        CommandRegistrationCallback.EVENT.register(commandManager::register);

        // flush notifier every few seconds, dictated by readoutWindowInSeconds config
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            int readoutWindowInSeconds = config.getReadoutWindowInSeconds();
            int tickDiff =
                server.getTickCount() %
                (TICKS_PER_SECOND * readoutWindowInSeconds);
            if (tickDiff == 0) {
                EventBuffer.flush();
                return;
            }
        });
    }

    private void initTargets(
        TargetRegistry registry,
        ModConfig config,
        PreferenceManager preferenceManager
    ) {
        if (config.isSendToConsole()) {
            ServerConsoleTarget consoleTarget = new ServerConsoleTarget();
            registry.register(consoleTarget);
        }
        if (config.isSendToIngame()) {
            ChatTarget chatTarget = new ChatTarget(preferenceManager);
            registry.register(chatTarget);
        }
        if (config.isSendToDiscord()) {
            DiscordTarget discordTarget = new DiscordTarget(
                config.getDiscordWebhookUrl()
            );
            discordTarget.testConnection();
            registry.register(discordTarget);
        }
    }

    private void initConfig(ModConfigManager configManager) throws IOException {
        configManager.load();
        ModConfig config = configManager.getConfig();

        int blockMapSize = config.getBlockMap().size();
        logger.info("{} blocks configured to trigger readouts.", blockMapSize);
    }

    private void initLogging(Level logLevel) {
        Configurator.setLevel(MOD_NAME, logLevel);
        if (logger.isDebugEnabled()) {
            logger.debug("Debug logging is enabled.");
        }
    }
}
