package com.github.Veivel.notifier.target;

import com.github.Veivel.event.ReadoutEvent;

/**
 * Interface for all Target implementations.
 */
public interface Target {
    public abstract void sendReadout(ReadoutEvent event);

    // Returns false if this Target is unhealthy or invalid, true otherwise.
    public boolean healthCheck();

    public TargetConfig getConfig();
}
