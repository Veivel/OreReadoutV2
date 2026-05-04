package com.github.Veivel.config;

import com.github.Veivel.orereadout.OreReadoutMod;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/** code adapted from https://github.com/DrexHD/Vanish/ */
public class ModConfigManager {

    private ModConfig config;
    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private static final Path OLD_CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("ore-readout.properties");
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("ore-readout.yml");

    public ModConfigManager() {
        this.config = new ModConfig();
    }

    public void load() throws IOException {
        logger.info("Loading configuration for OreReadoutV2...");

        // Warn if old config exists (ignore)
        if (OLD_CONFIG_PATH.toFile().exists()) {
            logger.warn(
                "A deprecated ore-readout.properties file was found. This file will be ignored."
            );
        }

        // First-time setup, create new config file
        if (!CONFIG_PATH.toFile().exists()) {
            logger.info("Creating new configuration file for OreReadoutV2!");
            writeDefaultConfig(CONFIG_PATH.toString());
        }

        Yaml yaml = new Yaml();
        FileReader reader = new FileReader(CONFIG_PATH.toFile());
        Map<String, Object> map = yaml.load(reader);

        // manual handling of incomplete YAML
        if (!map.containsKey(ModConfig.READOUT_WINDOW_KEY)) {
            logger.warn(
                "The {} configuration was not found",
                ModConfig.READOUT_WINDOW_KEY
            );
        }

        // populate ModConfig object
        this.config.parseMap(map);

        logger.info("Config for OreReadoutV2 loaded!");
        logger.debug(this.config);
    }

    private void writeDefaultConfig(String destinationConfigPath) {
        ModConfig config = new ModConfig();
        DumperOptions dumperOptions = new DumperOptions();

        // yaml dumper config
        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        representer.addClassTag(ReadoutTargetOptions.class, Tag.MAP);
        Yaml yaml = new Yaml(representer, dumperOptions);

        // default values
        ReadoutTargetOptions readoutTargetConfig = new ReadoutTargetOptions(
            true,
            true,
            false
        );
        config.setReadoutTargets(readoutTargetConfig);
        List<String> blocks = List.of(
            "diamond_ore",
            "ancient_debris",
            "deepslate_diamond_ore"
        );
        config.setBlocks(blocks);
        config.setDiscordWebhookUrl("https://discord.com/api/webhooks/xxx/xxx");
        Map<String, Object> map = config.toMap();

        String output = yaml.dump(map);
        logger.debug("YAML output: {}", output);

        // Write the YAML output to a file
        try (FileWriter writer = new FileWriter(destinationConfigPath)) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ModConfig getConfig() {
        return config;
    }
}
