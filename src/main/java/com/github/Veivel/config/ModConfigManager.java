package com.github.Veivel.config;

import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.github.Veivel.orereadout.OreReadoutMod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ModConfigManager {
    // code adapted from https://github.com/DrexHD/Vanish/

    private static final Logger LOGGER = OreReadoutMod.LOGGER;
    private static final Path OLD_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ore-readout.properties");
    private static final Path DEFAULT_CONFIG_PATH = Path.of("/data/config/default.ore-readout.yml");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ore-readout.yml");
    private static ModConfig config = new ModConfig();

    private ModConfigManager() {}

    public static void load() throws IOException {
        LOGGER.info("Loading configuration for OreReadoutV2...");

        if (OLD_CONFIG_PATH.toFile().exists()) {
          LOGGER.warn("A deprecated ore-readout.properties file was not found. This file will be ignored.");
        }
        
        if (!CONFIG_PATH.toFile().exists()) {
          LOGGER.info("Creating new configuration file for OreReadoutV2!");

          writeDefaultConfig(CONFIG_PATH.toString());
        }

        Yaml yaml = new Yaml();
        FileReader reader = new FileReader(CONFIG_PATH.toFile());
        Map<String, Object> map = yaml.load(reader);
        config.parseMap(map);

        Map<String, Boolean> blockMap = config.createBlockMapFromList(config.getBlocks());
        config.setBlockMap(blockMap);

        LOGGER.debug(config);
    }

    private static void writeDefaultConfig(String destinationConfigPath) {
        ModConfig config = new ModConfig();
        DumperOptions dumperOptions = new DumperOptions();

        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        representer.addClassTag(ReadoutTargetOptions.class, Tag.MAP);

        ReadoutTargetOptions readoutTargetConfig = new ReadoutTargetOptions(true, true, false);
        config.setReadoutTargets(readoutTargetConfig);
        List<String> blocks = List.of("diamond_ore", "ancient_debris", "deepslate_diamond_ore");
        config.setBlocks(blocks);
        config.setDiscordWebhookUrl("https://discord.com/api/webhooks/xxx/xxx");

        Yaml yaml = new Yaml(representer);
        Map<String, Object> map = config.toMap();
        String output = yaml.dump(map);
        LOGGER.debug("YAML output: {}", output);

        // Write the YAML output to a file
        try (FileWriter writer = new FileWriter(destinationConfigPath)) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ModConfig getConfig() {
        return config;
    }

}
