package com.github.Veivel.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class ModConfig {
  private static final String DISCORD_WEBHOOK_URL_KEY = "discordWebhookUrl";
  private static final String BLOCKS_KEY = "blocks";
  private static final String READOUT_TARGETS_KEY = "readoutTargets";

  private ReadoutTargetOptions readoutTargetConfig;
  private String discordWebhookUrl;
  private List<String> blocks;
  private Map<String, Boolean> blockMap;

  public ReadoutTargetOptions getReadoutTargets() {
    return readoutTargetConfig;
  }

  public boolean isSendToConsole() {
    return readoutTargetConfig.getConsole();
  }

  public boolean isSendToIngame() {
    return readoutTargetConfig.getIngame();
  }

  public boolean isSendToDiscord() {
    return readoutTargetConfig.getDiscord();
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

  public Map<String, Object> toMap() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(READOUT_TARGETS_KEY, getReadoutTargets());
    map.put(BLOCKS_KEY, getBlocks());
    map.put(DISCORD_WEBHOOK_URL_KEY, getDiscordWebhookUrl());

    return map;
  }

  @SuppressWarnings("unchecked")
  public void parseMap(Map<String, Object> map) {
    if (map.containsKey(DISCORD_WEBHOOK_URL_KEY)) {
      setDiscordWebhookUrl((String) map.get(DISCORD_WEBHOOK_URL_KEY));
    }
    if (map.containsKey(BLOCKS_KEY)) {
      // assuming type-cast is OK
      setBlocks((List<String>) map.get(BLOCKS_KEY));
    }
    if (map.containsKey(READOUT_TARGETS_KEY)) {
      // assuming type-cast is OK
      Map<String, Object> readoutMap = (Map<String, Object>) map.get(READOUT_TARGETS_KEY);

      boolean discord = Boolean.TRUE.equals(readoutMap.get("discord"));
      boolean console = Boolean.TRUE.equals(readoutMap.get("console"));
      boolean ingame = Boolean.TRUE.equals(readoutMap.get("ingame"));
      ReadoutTargetOptions readoutTargets = new ReadoutTargetOptions(console, ingame, discord);
      setReadoutTargets(readoutTargets);
    }
  }

  @Override
  public String toString() {
      Gson gson = new Gson();
      return gson.toJson(this);
  }
}
