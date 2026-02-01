package com.github.Veivel.notifier.sink;

import org.apache.logging.log4j.Logger;

import com.github.Veivel.orereadout.OreReadoutMod;

public class ConsoleSink {
  private Logger logger;

  public ConsoleSink() {
    setLogger(OreReadoutMod.LOGGER);
  }

  public Logger getLogger() {
    return this.logger;
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public void readOut(String playerName, int quantity, int x, int y, int z, String dimension) {
    Logger log = getLogger();
    log.info(
      "{} mined {} ores at [{} {} {}] in {}",
      playerName, quantity, x, y, z, dimension
    );
  }
}
