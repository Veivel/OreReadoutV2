package com.github.Veivel.config;

import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.github.Veivel.orereadout.OreReadout;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    // code adapted from https://github.com/DrexHD/Vanish/

    private static final Logger LOGGER = OreReadout.LOGGER;
    private static final Path OLD_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ore-readout.properties");
    private static final Path DEFAULT_CONFIG_PATH = Path.of("/data/orereadout/default.ore-readout.yml");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ore-readout.yml");
    private static ModConfig config = new ModConfig();

    private ConfigManager() {}

    public static void load() throws Exception {
        LOGGER.info("Loading configuration...");

        if (OLD_CONFIG_PATH.toFile().exists()) {
          LOGGER.warn("A deprecated ore-readout.properties file was not found. This file will be ignored.");
        }
        
        if (!CONFIG_PATH.toFile().exists()) {
          LOGGER.info("Creating new configuration file!");

          writeDefaultConfig(CONFIG_PATH.toString());
        }

        Yaml yaml = new Yaml();
        FileReader reader = new FileReader(CONFIG_PATH.toFile());
        Map<String, Object> data = yaml.load(reader);
            
        // Manually create and map the ModConfig instance
        if (data.containsKey("discordWebhookUrl")) {
            config.setDiscordWebhookUrl((String) data.get("discordWebhookUrl"));
        }
        if (data.containsKey("blocks")) {
            config.setBlocks((List<String>) data.get("blocks"));
        }
        if (data.containsKey("readoutTargets")) {
            // Assuming readoutTargets is a Map that can be used to build the ReadoutTargetConfig.
            Map<String, Object> readoutMap = (Map<String, Object>) data.get("readoutTargets");
            boolean discord = Boolean.TRUE.equals(readoutMap.get("discord")); // update based on your YAML structure
            boolean console = Boolean.TRUE.equals(readoutMap.get("console"));
            boolean ingame = Boolean.TRUE.equals(readoutMap.get("ingame"));
            ReadoutTargetOptions readoutTargets = new ReadoutTargetOptions(console, ingame, discord);
            config.setReadoutTargets(readoutTargets);
        }

        Map<String, Boolean> blockMap = config.createBlockMapFromList(config.getBlocks());
        config.setBlockMap(blockMap);

        LOGGER.debug(config);
    }

    public static void writeDefaultConfig(String destinationConfigPath) {
        ModConfig config = new ModConfig();
        DumperOptions dumperOptions = new DumperOptions();
        Map<String, Object> map = new LinkedHashMap<>();

        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer(dumperOptions);
        representer.addClassTag(ReadoutTargetOptions.class, Tag.MAP);
        // representer.getPropertyUtils().setSkipMissingProperties(true);

        ReadoutTargetOptions readoutTargetConfig = new ReadoutTargetOptions(true, true, false);
        config.setReadoutTargets(readoutTargetConfig);
        List<String> blocks = List.of("diamond_ore", "ancient_debris", "deepslate_diamond_ore");
        config.setBlocks(blocks);
        config.setDiscordWebhookUrl("https://discord.com/api/webhooks/xxx/xxx");

        map.put("readoutTargets", config.getReadoutTargets());
        map.put("blocks", config.getBlocks());
        map.put("discordWebhookUrl", config.getDiscordWebhookUrl());

        Yaml yaml = new Yaml(representer);
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
