package com.github.Veivel.notifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.github.Veivel.config.YamlConfigManager;
import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.TargetRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventBufferTest {

    private static final String CONFIG_FIXTURE = "event-buffer-test.yaml";
    private static final String CONFIG_FILE_NAME = "ore-readout-v2.yaml";

    // Fixture sets blocks_broken_threshold to this value.
    private static final int THRESHOLD = 2;

    private static final String MATCHING_BLOCK = "diamond_ore";
    private static final String PLAYER_UUID = "uuid-steve";
    private static final String PLAYER_NAME = "Steve";
    private static final String DIMENSION = "minecraft:overworld";

    @Mock
    private TargetRegistry targetRegistry;

    private EventBuffer eventBuffer;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        Path configPath = tempDir.resolve(CONFIG_FILE_NAME);
        try (
            InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("config/" + CONFIG_FIXTURE)
        ) {
            assertThat(stream)
                .as(
                    "test fixture config/%s must exist on the classpath",
                    CONFIG_FIXTURE
                )
                .isNotNull();
            Files.copy(stream, configPath);
        }

        YamlConfigManager configManager = new YamlConfigManager(configPath);
        eventBuffer = new EventBuffer(configManager, targetRegistry);
        // EventBuffer's constructor registers an onAfterReload listener that
        // populates its internal block set. Calling load() fires that listener
        // just as real mod initialization would.
        configManager.load();
    }

    private MixinEvent eventAt(int x, int y, int z) {
        return new MixinEvent(
            PLAYER_UUID,
            PLAYER_NAME,
            MATCHING_BLOCK,
            DIMENSION,
            x,
            y,
            z
        );
    }

    @Test
    void dispatch_doesNotEmitReadout_whenQuantityIsBelowThreshold() {
        // Threshold = 2; a single buffered event leaves quantity at 1, so
        // dispatch should short-circuit before calling targetRegistry.emit.
        eventBuffer.checkAndBuffer(eventAt(10, 20, 30));
        eventBuffer.flush();

        verifyNoInteractions(targetRegistry);
    }

    @Test
    void dispatch_emitsReadout_whenQuantityEqualsThreshold() {
        eventBuffer.checkAndBuffer(eventAt(10, 20, 30));
        eventBuffer.checkAndBuffer(eventAt(10, 20, 30));
        eventBuffer.flush();

        ArgumentCaptor<ReadoutEvent> emitted = ArgumentCaptor.forClass(
            ReadoutEvent.class
        );
        verify(targetRegistry).emit(emitted.capture());

        ReadoutEvent readout = emitted.getValue();
        assertThat(readout.playerName).isEqualTo(PLAYER_NAME);
        assertThat(readout.quantity).isEqualTo(THRESHOLD);
        assertThat(readout.x).isEqualTo(10);
        assertThat(readout.y).isEqualTo(20);
        assertThat(readout.z).isEqualTo(30);
        assertThat(readout.dimension).isEqualTo(DIMENSION);
    }

    @Test
    void dispatch_emitsReadout_whenQuantityIsAboveThreshold() {
        // Three events at distinct coordinates: quantity = 3 (above threshold)
        // and the aggregate position should track the latest event.
        eventBuffer.checkAndBuffer(eventAt(10, 20, 30));
        eventBuffer.checkAndBuffer(eventAt(11, 21, 31));
        eventBuffer.checkAndBuffer(eventAt(12, 22, 32));
        eventBuffer.flush();

        ArgumentCaptor<ReadoutEvent> emitted = ArgumentCaptor.forClass(
            ReadoutEvent.class
        );
        verify(targetRegistry).emit(emitted.capture());

        ReadoutEvent readout = emitted.getValue();
        assertThat(readout.playerName).isEqualTo(PLAYER_NAME);
        assertThat(readout.quantity).isEqualTo(3);
        assertThat(readout.x).isEqualTo(12);
        assertThat(readout.y).isEqualTo(22);
        assertThat(readout.z).isEqualTo(32);
        assertThat(readout.dimension).isEqualTo(DIMENSION);
    }
}
