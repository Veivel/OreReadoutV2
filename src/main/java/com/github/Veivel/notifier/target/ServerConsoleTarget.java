package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.orereadout.OreReadoutMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConsoleTarget extends AbstractTarget {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private final String targetCode = "server_console";

    public ServerConsoleTarget() {}

    public void sendReadout(ReadoutEvent event) {
        // The main Logger happens to log to the server console
        logger.info(
            "{} mined {} ores at [{} {} {}] in {}",
            event.playerName,
            event.quantity,
            event.x,
            event.y,
            event.z,
            event.dimension
        );
    }
}
