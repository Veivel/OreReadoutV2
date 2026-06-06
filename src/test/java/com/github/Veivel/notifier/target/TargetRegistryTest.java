package com.github.Veivel.notifier.target;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.console.ServerConsoleConfig;
import com.github.Veivel.server.PreferenceManager;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TargetRegistryTest {

    private static final String VALID_TARGET_NAME = "server-console";
    private static final String UNKNOWN_TARGET_NAME = "nonexistent";

    @Mock
    private ConfigManager configManager;

    @Mock
    private PreferenceManager preferenceManager;

    private TargetRegistry registry;
    private Runnable reloadListener;

    @BeforeEach
    void setUp() {
        registry = new TargetRegistry(configManager, preferenceManager);

        // The constructor is expected to register a reload listener so that
        // config reloads refresh the registry. Capture it for tests that
        // exercise the reload lifecycle.
        ArgumentCaptor<Runnable> listenerCaptor = ArgumentCaptor.forClass(
            Runnable.class
        );
        verify(configManager).onAfterReload(listenerCaptor.capture());
        reloadListener = listenerCaptor.getValue();
    }

    private ModConfig modConfigWith(List<TargetConfig> targets) {
        // Only `targets` is exercised by TargetRegistry; the remaining fields
        // are placeholders to satisfy the record constructor.
        return new ModConfig(3, targets, Set.of(), null, null, false);
    }

    private void stubConfigTargets(List<TargetConfig> targets) {
        when(configManager.get()).thenReturn(modConfigWith(targets));
    }

    @Test
    void constructor_initializesEmptyRegistryAndRegistersReloadListener() {
        assertThat(registry.size()).isZero();
        assertThat(reloadListener).isNotNull();
    }

    @Test
    void load_withZeroTargets_registersNone() {
        stubConfigTargets(List.of());

        registry.load();

        assertThat(registry.size()).isZero();
    }

    @Test
    void load_withOneEnabledAndOneDisabledTarget_registersOnlyTheEnabledOne() {
        stubConfigTargets(
            List.of(
                new ServerConsoleConfig(VALID_TARGET_NAME, true),
                new ServerConsoleConfig(VALID_TARGET_NAME, false)
            )
        );

        registry.load();

        assertThat(registry.size()).isEqualTo(1);
    }

    @Test
    void load_withOneValidAndOneUnknownNameTarget_registersOnlyTheValidOne() {
        TargetConfig unknown = mock(TargetConfig.class);
        when(unknown.name()).thenReturn(UNKNOWN_TARGET_NAME);

        stubConfigTargets(
            List.of(new ServerConsoleConfig(VALID_TARGET_NAME, true), unknown)
        );

        registry.load();

        assertThat(registry.size()).isEqualTo(1);
    }

    @Test
    void onAfterReload_invokedTwice_cleansAndReloadsWithNewConfig() {
        ModConfig firstConfig = modConfigWith(
            List.of(
                new ServerConsoleConfig(VALID_TARGET_NAME, true),
                new ServerConsoleConfig(VALID_TARGET_NAME, true)
            )
        );
        ModConfig secondConfig = modConfigWith(
            List.of(new ServerConsoleConfig(VALID_TARGET_NAME, true))
        );
        when(configManager.get()).thenReturn(firstConfig, secondConfig);

        // First reload also doubles as the "two valid targets both register"
        // check — load()'s loop must accumulate, not overwrite.
        reloadListener.run();
        assertThat(registry.size()).isEqualTo(2);

        // Without cleanup() the second reload would accumulate to size = 3.
        // Asserting size = 1 proves the listener clears existing targets
        // before re-running load() against the fresh config.
        reloadListener.run();
        assertThat(registry.size()).isEqualTo(1);
    }

    @Test
    void emit_dispatchesEventToEveryRegisteredTarget() {
        Target firstTarget = mock(Target.class);
        Target secondTarget = mock(Target.class);
        when(firstTarget.healthCheck()).thenReturn(true);
        when(secondTarget.healthCheck()).thenReturn(true);
        registry.register(firstTarget);
        registry.register(secondTarget);

        ReadoutEvent event = new ReadoutEvent(
            "Steve",
            3,
            1,
            2,
            3,
            "minecraft:overworld"
        );
        registry.emit(event);

        verify(firstTarget).sendReadout(event);
        verify(secondTarget).sendReadout(event);
    }

    @Test
    void size_reflectsTheNumberOfRegisteredTargets() {
        assertThat(registry.size()).isZero();

        Target firstTarget = mock(Target.class);
        Target secondTarget = mock(Target.class);
        when(firstTarget.healthCheck()).thenReturn(true);
        when(secondTarget.healthCheck()).thenReturn(true);

        registry.register(firstTarget);
        assertThat(registry.size()).isEqualTo(1);

        registry.register(secondTarget);
        assertThat(registry.size()).isEqualTo(2);
    }

    @Test
    void register_withFailingHealthCheck_skipsTheTarget() {
        Target unhealthyTarget = mock(Target.class);
        when(unhealthyTarget.healthCheck()).thenReturn(false);

        registry.register(unhealthyTarget);

        assertThat(registry.size()).isZero();

        // A subsequent emit must not reach a target that failed health check.
        registry.emit(
            new ReadoutEvent("Steve", 1, 0, 0, 0, "minecraft:overworld")
        );
        verify(unhealthyTarget, never()).sendReadout(any());
    }

    @Test
    void unregister_removesTargetAndDecrementsSize() {
        Target target = mock(Target.class);
        when(target.healthCheck()).thenReturn(true);
        registry.register(target);
        assertThat(registry.size()).isEqualTo(1);

        registry.unregister(target);

        assertThat(registry.size()).isZero();

        // After unregister the target must no longer receive events.
        registry.emit(
            new ReadoutEvent("Steve", 1, 0, 0, 0, "minecraft:overworld")
        );
        verify(target, never()).sendReadout(any());
    }
}
