package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;

/** Abstract base class for all sink implementations. */
public interface Target {
    public abstract void sendReadout(ReadoutEvent event);

    // TODO: implement health check fo all targets, run during init
    // public boolean healthCheck();

    public TargetConfig getConfig();
}
