package com.github.Veivel.config;

import java.io.IOException;

public interface ConfigManager {
    // Loads a new ModConfig record, this also runs during reload
    void load() throws IOException;

    // Returns the current ModConfig instance
    ModConfig get();

    // Registers a new listener that will fire after load/reload.
    // To be called by consumers of ConfigManager
    void onAfterReload(Runnable listener);
}
