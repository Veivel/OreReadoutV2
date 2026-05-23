package com.github.Veivel.notifier.target.console;

import com.github.Veivel.notifier.target.TargetConfig;

public record ServerConsoleConfig(
    String name,
    boolean enabled
) implements TargetConfig {}
