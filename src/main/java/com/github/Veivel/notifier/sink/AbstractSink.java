package com.github.Veivel.notifier.sink;

import org.apache.logging.log4j.Logger;

/** Abstract base class for all sink implementations. */
public abstract class AbstractSink {

  private Logger logger;

  public AbstractSink() {}

  public Logger getLogger() {
    return this.logger;
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public abstract void readOut(
    String playerName,
    int quantity,
    int x,
    int y,
    int z,
    String dimension
  );
}
