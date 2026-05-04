package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;
import org.apache.logging.log4j.Logger;

/** Abstract base class for all sink implementations. */
public abstract class AbstractTarget {

    private final Logger logger;
    private final String targetCode;

    public AbstractTarget() {
        this.logger = null;
        this.targetCode = "";
    }

    public abstract void sendReadout(ReadoutEvent event);
}
