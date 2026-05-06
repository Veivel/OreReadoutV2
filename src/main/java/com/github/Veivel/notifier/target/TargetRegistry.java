package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.orereadout.OreReadoutMod;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetRegistry {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private Integer size;
    private List<AbstractTarget> targets;

    public TargetRegistry() {
        targets = new ArrayList<AbstractTarget>();
        size = 0;
    }

    public void cleanup() {
        targets.clear();
        size = 0;
    }

    public void register(AbstractTarget target) {
        logger.debug(
            String.format(
                "Registering target %s...",
                target.getClass().getName(),
                null
            )
        );
        targets.add(target);
        size += 1;
    }

    public void unregister(AbstractTarget target) {
        targets.remove(target);
        size -= 1;
    }

    public Integer size() {
        return size;
    }

    public void emit(ReadoutEvent event) {
        logger.debug(String.format("Emitting event to %d targets...", size));
        for (AbstractTarget target : targets) {
            target.sendReadout(event);
        }
    }
}
