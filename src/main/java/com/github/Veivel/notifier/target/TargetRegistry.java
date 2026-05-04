package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;
import java.util.ArrayList;
import java.util.List;

public class TargetRegistry {

    private List<AbstractTarget> targets;

    public TargetRegistry() {
        targets = new ArrayList<AbstractTarget>();
    }

    public void cleanup() {
        targets.clear();
    }

    public void register(AbstractTarget target) {
        targets.add(target);
    }

    public void unregister(AbstractTarget target) {
        targets.remove(target);
    }

    public void emit(ReadoutEvent event) {
        for (AbstractTarget target : targets) {
            target.sendReadout(event);
        }
    }
}
