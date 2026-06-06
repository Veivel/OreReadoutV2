package com.github.Veivel.logger;

import com.github.Veivel.orereadout.OreReadoutMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogger {

    public static Logger get() {
        return LogManager.getLogger(OreReadoutMod.MOD_NAME);
    }
}
