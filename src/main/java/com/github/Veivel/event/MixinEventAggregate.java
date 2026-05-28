package com.github.Veivel.event;

// TODO: determine if we can merge into ReadoutEvent class
// They server different business logic purposes, but logistically they are almost the same

public record MixinEventAggregate(
    String playerUuid,
    String playerName,
    int quantity,
    String dimensionName,
    int x,
    int y,
    int z
) {
    public static MixinEventAggregate of(MixinEvent e) {
        return new MixinEventAggregate(
            e.playerUuid(),
            e.playerName(),
            1,
            e.dimensionName(),
            e.x(),
            e.y(),
            e.z()
        );
    }

    public MixinEventAggregate aggregate(MixinEvent e) {
        return new MixinEventAggregate(
            playerUuid,
            playerName,
            quantity + 1,
            e.dimensionName(),
            e.x(),
            e.y(),
            e.z()
        );
    }
}
