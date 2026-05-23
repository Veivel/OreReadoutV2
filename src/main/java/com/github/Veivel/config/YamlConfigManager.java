package com.github.Veivel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.Veivel.orereadout.OreReadoutMod;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
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

    public YamlConfigManager(Path path) {
        configPath = path;
    }

    public void load() throws IOException {
        logger.info("Loading configuration for OreReadoutV2...");

        // First-time setup, create new config file
        if (!configPath.toFile().exists()) {
            logger.info("Creating new configuration file for OreReadoutV2!");
            writeDefaultConfig(configPath.toString());
        }

        this.config = parseRawMap();

        logger.info("Config for OreReadoutV2 loaded!");
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

    private ModConfig parseRawMap() {
        // int version = 3;
        // if (map.containsKey("config_version")) {
        //     String versionString = map.get("config_version").toString();
        //     version = Integer.parseInt(versionString);
        // }

        // Map<String, TargetConfig> targets = new HashMap<String, TargetConfig>();
        // if (map.containsKey(READOUT_TARGETS_KEY)) {
        //     Map<String, Object> targetsRaw = (Map<String, Object>) map.get(
        //         READOUT_TARGETS_KEY
        //     );
        //     targetsRaw.forEach((targetName, targetObject) -> {
        //         targets.put(targetName, (TargetConfig) targetObject);
        //     });
        // }

        // Set<String> blockSet = null;
        // if (map.containsKey(READOUT_BLOCKS_KEY)) {
        //     List<String> blockList = (List<String>) map.get(READOUT_BLOCKS_KEY);
        //     blockSet = HashSet.newHashSet(blockList.size());
        //     blockSet.addAll(blockList);
        // }

        // int readoutWindow = 7;
        // if (map.containsKey(READOUT_WINDOW_KEY)) {
        //     String readoutWindowString = map.get(READOUT_WINDOW_KEY).toString();
        //     readoutWindow = Integer.parseInt(readoutWindowString);
        // }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        // mapper = mapper.setPropertyNamingStrategy(
        //     PropertyNamingStrategies.SNAKE_CASE
        // );
        try {
            ModConfig config = mapper.readValue(
                configPath.toFile(),
                ModConfig.class
            );
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeDefaultConfig(String destinationConfigPath) {
        // TODO: fix, copy example.yaml to destination
        throw new NotImplementedException("Config is not present");
    }
}
