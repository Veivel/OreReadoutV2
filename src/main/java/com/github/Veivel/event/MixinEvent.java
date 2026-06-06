package com.github.Veivel.event;

public record MixinEvent(
    String playerUuid,
    String playerName,
    String blockName,
    // Attributes below currently unused
    String dimensionName,
    int x,
    int y,
    int z
) {}
