package com.github.Veivel.notifier.target.console;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.Target;
import com.github.Veivel.notifier.target.TargetConfig;
import org.apache.logging.log4j.Logger;

public class ServerConsoleTarget implements Target {

    private final Logger logger = ModLogger.get();
    private ServerConsoleConfig config;

    public ServerConsoleTarget(ServerConsoleConfig config) {
        this.config = config;
    }

    public boolean healthCheck() {
        if (config == null) {
            return false;
        }
        return true;
    }

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

    @Override
    public TargetConfig getConfig() {
        return config;
    }
}
