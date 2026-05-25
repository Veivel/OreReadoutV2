package com.github.Veivel.notifier.target;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.chat.ChatConfig;
import com.github.Veivel.notifier.target.chat.ChatTarget;
import com.github.Veivel.notifier.target.console.ServerConsoleConfig;
import com.github.Veivel.notifier.target.console.ServerConsoleTarget;
import com.github.Veivel.notifier.target.discord.DiscordConfig;
import com.github.Veivel.notifier.target.discord.DiscordTarget;
import com.github.Veivel.orereadout.OreReadoutMod;
import com.github.Veivel.server.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetRegistry {

    private final Logger logger = LogManager.getLogger(OreReadoutMod.MOD_NAME);
    private Integer size;
    private List<Target> targets;
    private Map<String, Function<TargetConfig, Target>> factories = new HashMap<
        String,
        Function<TargetConfig, Target>
    >();

    private ConfigManager configManager;
    private PreferenceManager preferenceManager;

    public TargetRegistry(
        ConfigManager configManager,
        PreferenceManager preferenceManager
    ) {
        this.configManager = configManager;
        this.preferenceManager = preferenceManager;
        targets = new ArrayList<Target>();
        size = 0;

        initFactories();
        configManager.onAfterReload(() -> {
            cleanup();
            load();
        });
    }

    public void load() {
        List<TargetConfig> targetConfigs = configManager.get().targets();
        targetConfigs.forEach(targetConfig -> {
            String targetName = targetConfig.name();
            if (factories.containsKey(targetName) && targetConfig.enabled()) {
                Function<TargetConfig, Target> factory = factories.get(
                    targetName
                );
                Target target = factory.apply(targetConfig);
                register(target);
            }
        });
        return;
    }

    public void initFactories() {
        factories.put("discord", cfg -> new DiscordTarget((DiscordConfig) cfg));
        factories.put("server-chat", cfg ->
            new ChatTarget((ChatConfig) cfg, preferenceManager)
        );
        factories.put("server-console", cfg ->
            new ServerConsoleTarget((ServerConsoleConfig) cfg)
        );
    }

    public void cleanup() {
        targets.clear();
        size = 0;
    }

    public void register(Target target) {
        logger.debug(
            "Registering target to TargetRegistry: {}",
            target.getClass().getName()
        );
        targets.add(target);
        size += 1;
    }

    public void unregister(Target target) {
        targets.remove(target);
        size -= 1;
    }

    public Integer size() {
        return size;
    }

    public void emit(ReadoutEvent event) {
        logger.debug(String.format("Emitting event to %d targets...", size));
        for (Target target : targets) {
            target.sendReadout(event);
        }
    }
}
