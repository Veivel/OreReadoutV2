package com.github.Veivel.event;

public class ReadoutEvent {

    public String playerName;
    public int quantity;
    public double x;
    public double y;
    public double z;
    public String dimension;

    public ReadoutEvent(
        String playerName,
        int quantity,
        double x,
        double y,
        double z,
        String dimension
    ) {
        this.playerName = playerName;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public void truncateCoordinates() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
    }
}
