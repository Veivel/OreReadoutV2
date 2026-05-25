package com.github.Veivel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.Veivel.orereadout.OreReadoutMod;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YamlConfigManager implements ConfigManager {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private final List<Runnable> listeners = new ArrayList<>();
    private ModConfig config;
    private Path configPath;

    // TODO: move this somewhere else
    public static final String READOUT_BLOCKS_KEY = "readout_blocks";
    public static final String READOUT_TARGETS_KEY = "targets";
    public static final String READOUT_WINDOW_KEY = "readout_window_seconds";
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
        logger.debug(this.config);

        // Run all `onAfterReload` listeners
        listeners.forEach(Runnable::run);
        return;
    }

    public void onAfterReload(Runnable listener) {
        listeners.add(listener);
    }

    public ModConfig get() {
        return this.config;
    }

    private ModConfig readConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ModConfig config = mapper.readValue(
                configPath.toFile(),
                ModConfig.class
            );
            return config;
        } catch (IOException e) {
            throw e;
        }
    }

    private void writeDefaultConfig(String internalResourceName) {
        Path destinationPath = configPath;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream stream = classLoader.getResourceAsStream(
                internalResourceName
            );
            Files.copy(stream, destinationPath);
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
