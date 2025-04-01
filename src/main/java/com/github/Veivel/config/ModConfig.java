package com.github.Veivel.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class ModConfig {
  private ReadoutTargetOptions readoutTargetConfig;
  private String discordWebhookUrl;
  private List<String> blocks;
  private Map<String, Boolean> blockMap;

  public ReadoutTargetOptions getReadoutTargets() {
    return readoutTargetConfig;
  }

  public boolean getSendToConsole() {
    return readoutTargetConfig.console;
  }

  public boolean getSendToIngame() {
    return readoutTargetConfig.ingame;
  }

  public boolean getSendToDiscord() {
    return readoutTargetConfig.discord;
  }

  public void setReadoutTargets(ReadoutTargetOptions readoutTargetConfig) {
    this.readoutTargetConfig = readoutTargetConfig;
  }

  public String getDiscordWebhookUrl() {
    return discordWebhookUrl;
  }

  public void setDiscordWebhookUrl(String discordWebhookUrl) {
    this.discordWebhookUrl = discordWebhookUrl;
  }

  public List<String> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<String> blocks) {
    this.blocks = blocks;
  }

  public Map<String, Boolean> getBlockMap() {
    return blockMap;
  }

  public void setBlockMap(Map<String, Boolean> blockMap) {
    this.blockMap = blockMap;
  }

  public Map<String, Boolean> createBlockMapFromList(List<String> blocks) {
    HashMap<String, Boolean> resultMap = new HashMap<>();
    blocks.forEach(key -> resultMap.put(key, true));

    return resultMap;
  }

  @Override
  public String toString() {
      Gson gson = new Gson();
      return gson.toJson(this);
  }
}
