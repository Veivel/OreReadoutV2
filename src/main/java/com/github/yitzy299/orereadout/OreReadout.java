package com.github.yitzy299.orereadout;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.github.yitzy299.orereadout.DiscordWebhookSender;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class OreReadout implements ModInitializer {
    public static Logger LOG = LogManager.getLogger();
    public static DiscordWebhookSender discordWebhookSender = null;
    public static boolean sendToChat = false;
    public static boolean sendInConsole = true;
    public static boolean sendToDiscord = false;
    public static String discordWebhookUrl = "";
    public static String blocks;

    @Override
    public void onInitialize() {
        Path configPath = Paths.get(FabricLoader.getInstance().getConfigDir().toAbsolutePath().toString() + "/ore-readout.properties");
        if (!configPath.toFile().exists()) {
            try {
                Files.copy(OreReadout.class.getResourceAsStream("/data/ore-readout/default_config.properties"), configPath);
                LOG.info("Config file for ore-readout created in config/ore-readout.properties");
                readProperties();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            readProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readProperties() throws IOException {
        InputStream inputStream = new FileInputStream(
            FabricLoader.getInstance().getConfigDir().toAbsolutePath().toString() + "/ore-readout.properties"
        );
        Properties props = new Properties();
        props.load(inputStream);

        sendInConsole = props.getProperty("send_to_console", "true").equals("true");
        sendToChat = props.getProperty("send_to_chat").equals("true");
        sendToDiscord = props.getProperty("send_to_discord").equals("true");
        discordWebhookUrl = props.getProperty("discord_webhook_url", "N/A");
        if (sendToDiscord && discordWebhookUrl.equals("N/A")) {
            throw new IOException("Configuration error: send_to_discord is true but discord_webhook_url is blank");
        }
        discordWebhookSender = new DiscordWebhookSender(OreReadout.discordWebhookUrl);
        blocks = props.getProperty("blocks");
    }
}
