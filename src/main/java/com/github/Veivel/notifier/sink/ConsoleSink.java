package com.github.Veivel.notifier.sink;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.orereadout.OreReadoutMod;
import org.apache.logging.log4j.Logger;

public class ConsoleSink extends AbstractSink {

    public ConsoleSink() {
        super();
        setLogger(OreReadoutMod.LOGGER);
    }

    public void sendReadout(ReadoutEvent event) {
        Logger log = getLogger();
        log.info(
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
