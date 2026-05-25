package com.github.Veivel.logger;

import com.github.Veivel.config.ConfigManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class ModLogInitializer {

    private Logger logger;
    private String name;

    public ModLogInitializer(String name, ConfigManager configManager) {
        this.name = name;
        this.logger = LogManager.getLogger(name);
        Configurator.setLevel(this.logger, Level.INFO);
        configManager.onAfterReload(() -> {
            // refresh debug mode on every reload
            setLogLevel(configManager.get().debugMode());
        });
    }

    private void setLogLevel(boolean isDebugEnabled) {
        if (isDebugEnabled) {
            Configurator.setLevel(name, Level.DEBUG);
            logger.debug("Debug logging is enabled.");
        } else {
            Configurator.setLevel(name, Level.INFO);
        }
    }
}
