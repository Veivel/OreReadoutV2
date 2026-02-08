package com.github.Veivel.notifier.sink;

import com.github.Veivel.orereadout.OreReadoutMod;
import org.apache.logging.log4j.Logger;

public class ConsoleSink extends AbstractSink {

  public ConsoleSink() {
    super();
    setLogger(OreReadoutMod.LOGGER);
  }

  public void readOut(
    String playerName,
    int quantity,
    int x,
    int y,
    int z,
    String dimension
  ) {
    Logger log = getLogger();
    log.info(
      "{} mined {} ores at [{} {} {}] in {}",
      playerName,
      quantity,
      x,
      y,
      z,
      dimension
    );
  }
}
