package com.github.Veivel.config;

import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.TargetConfig;
import com.github.Veivel.notifier.target.TargetConfigSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class YamlConfigManager implements ConfigManager {

    private final Logger logger = ModLogger.get();
    private final List<Runnable> listeners = new ArrayList<>();
    private ModConfig config;
    private Path configPath;

    public static final String DEFAULT_CONFIG_RESOURCE =
        "data/config/default.yaml";

    public YamlConfigManager(Path path) {
        configPath = path;
    }

    public void load() throws IOException {
        logger.info("Loading configuration for OreReadoutV2...");

        // First-time setup, create new config file
        if (!configPath.toFile().exists()) {
            logger.info("Creating new configuration file for OreReadoutV2!");
            writeDefaultConfig(DEFAULT_CONFIG_RESOURCE);
        }

        try {
            this.config = readConfig();
        } catch (IOException e) {
            logger.error(
                "OreReadoutV2 config file {} could not be loaded.",
                configPath.toString()
            );
            throw e;
        }

        int blockCount = this.config.readoutBlockSet().size();
        int targetCount = this.config.targets().size();
        logger.info(
            "Config for OreReadoutV2 loaded with {} blocks for readout and {} targets!",
            blockCount,
            targetCount
        );

        // Run all `onAfterReload` listeners
        listeners.forEach(Runnable::run);
        logger.debug("Config: {}", this.config);

        return;
    }

    public void onAfterReload(Runnable listener) {
        listeners.add(listener);
    }

    public ModConfig get() {
        return this.config;
    }

    private ModConfig readConfig() throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .defaultOptions(opts ->
                opts.serializers(build ->
                    // We use registerExact in place of register to avoid recursion when our
                    // custom serializer returns a concrete class implementing TargetConfig
                    build.registerExact(
                        TargetConfig.class,
                        TargetConfigSerializer.INSTANCE
                    )
                )
            )
            .path(configPath)
            .build();

        try {
            ConfigurationNode node = loader.load();
            ModConfig config = node.get(ModConfig.class);
            config.validate();
            return config;
        } catch (IOException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        }
    }

    // We take in a String for the params because we
    // use a Resource rather than a Path.
    private void writeDefaultConfig(String internalResourceName) {
        Path destinationPath = configPath;
        InputStream stream;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            stream = classLoader.getResourceAsStream(internalResourceName);
            Files.copy(stream, destinationPath);
            stream.close();
        } catch (NullPointerException e) {
            logger.error(
                "Could not obtain obtain default config YAML as stream: {}",
                e.getMessage()
            );
        } catch (IOException e) {
            // Also catches FileAlreadyExistsException
            logger.error(
                "Could not write default config YAML at {}: {}",
                destinationPath.toString(),
                e.getMessage()
            );
        }
    }
}
