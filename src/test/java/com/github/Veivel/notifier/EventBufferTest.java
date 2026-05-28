package com.github.Veivel.notifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.Veivel.config.ConfigManager;
import com.github.Veivel.config.ModConfig;
import com.github.Veivel.event.MixinEvent;
import com.github.Veivel.event.ReadoutEvent;
import com.github.Veivel.notifier.target.TargetRegistry;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventBufferTest {

    private static final String MATCHING_BLOCK = "diamond_ore";
    private static final String PLAYER_UUID = "uuid-steve";
    private static final String PLAYER_NAME = "Steve";
    private static final String DIMENSION = "minecraft:overworld";

    @Mock
    private ConfigManager configManager;

    @Mock
    private TargetRegistry targetRegistry;

    private EventBuffer eventBuffer;

    @BeforeEach
    void setUp() {
        ModConfig config = new ModConfig(
            1,
            List.of(),
            Set.of(MATCHING_BLOCK),
            5,
            false
        );
        when(configManager.get()).thenReturn(config);

        // EventBuffer registers an onAfterReload listener in its constructor and
        // populates its internal block set only when that listener fires. We capture
        // and invoke it manually here to simulate the initial config load that
        // ConfigManager.load() would normally trigger in production.
        ArgumentCaptor<Runnable> reloadListener = ArgumentCaptor.forClass(
            Runnable.class
        );
        eventBuffer = new EventBuffer(configManager, targetRegistry);
        verify(configManager).onAfterReload(reloadListener.capture());
        reloadListener.getValue().run();
    }

    @Test
    void checkAndBuffer_emitsReadoutForMatchingBlock_onFlush() {
        MixinEvent event = new MixinEvent(
            PLAYER_UUID,
            PLAYER_NAME,
            MATCHING_BLOCK,
            DIMENSION,
            10,
            20,
            30
        );

        eventBuffer.checkAndBuffer(event);
        eventBuffer.flush();

        ArgumentCaptor<ReadoutEvent> emitted = ArgumentCaptor.forClass(
            ReadoutEvent.class
        );
        verify(targetRegistry).emit(emitted.capture());

        ReadoutEvent readout = emitted.getValue();
        assertThat(readout.playerName).isEqualTo(PLAYER_NAME);
        assertThat(readout.quantity).isEqualTo(1);
        assertThat(readout.x).isEqualTo(10);
        assertThat(readout.y).isEqualTo(20);
        assertThat(readout.z).isEqualTo(30);
        assertThat(readout.dimension).isEqualTo(DIMENSION);
    }

    @Test
    void checkAndBuffer_aggregatesTwoEventsFromSamePlayer_onFlush() {
        MixinEvent firstEvent = new MixinEvent(
            PLAYER_UUID,
            PLAYER_NAME,
            MATCHING_BLOCK,
            DIMENSION,
            10,
            20,
            30
        );
        MixinEvent secondEvent = new MixinEvent(
            PLAYER_UUID,
            PLAYER_NAME,
            MATCHING_BLOCK,
            DIMENSION,
            11,
            21,
            31
        );

        eventBuffer.checkAndBuffer(firstEvent);
        eventBuffer.checkAndBuffer(secondEvent);
        eventBuffer.flush();

        ArgumentCaptor<ReadoutEvent> emitted = ArgumentCaptor.forClass(
            ReadoutEvent.class
        );
        verify(targetRegistry).emit(emitted.capture());

        ReadoutEvent readout = emitted.getValue();
        assertThat(readout.playerName).isEqualTo(PLAYER_NAME);
        assertThat(readout.quantity).isEqualTo(2);
        assertThat(readout.x).isEqualTo(11);
        assertThat(readout.y).isEqualTo(21);
        assertThat(readout.z).isEqualTo(31);
        assertThat(readout.dimension).isEqualTo(DIMENSION);
    }
}
