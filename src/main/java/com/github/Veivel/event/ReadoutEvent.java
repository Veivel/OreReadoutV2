package com.github.Veivel.event;

public class ReadoutEvent {

    public String playerName;
    public int quantity;
    public int x;
    public int y;
    public int z;
    public String dimension;

    public ReadoutEvent(
        String playerName,
        int quantity,
        double x,
        double y,
        double z,
        String dimension
    ) {
        this(playerName, quantity, (int) x, (int) y, (int) z, dimension);
    }

    public ReadoutEvent(
        String playerName,
        int quantity,
        int x,
        int y,
        int z,
        String dimension
    ) {
        this.playerName = playerName;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
}
