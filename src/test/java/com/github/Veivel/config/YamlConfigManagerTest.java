package com.github.Veivel.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.Veivel.notifier.target.TargetConfig;
import com.github.Veivel.notifier.target.chat.ChatConfig;
import com.github.Veivel.notifier.target.console.ServerConsoleConfig;
import com.github.Veivel.notifier.target.discord.DiscordConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class YamlConfigManagerTest {

    private static final String CONFIG_FILE_NAME = "ore-readout-v2.yaml";

    private Path copyFixture(String fixtureName, Path tempDir)
        throws IOException {
        Path destination = tempDir.resolve(CONFIG_FILE_NAME);
        try (
            InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("config/" + fixtureName)
        ) {
            assertThat(stream)
                .as(
                    "test fixture config/%s must exist on the classpath",
                    fixtureName
                )
                .isNotNull();
            Files.copy(stream, destination);
        }
        return destination;
    }

    @Test
    void load_withValidYaml_populatesModConfig(@TempDir Path tempDir)
        throws IOException {
        Path configPath = copyFixture("valid.yaml", tempDir);

        YamlConfigManager manager = new YamlConfigManager(configPath);
        manager.load();

        ModConfig config = manager.get();
        assertThat(config).isNotNull();
        assertThat(config.configVersion()).isEqualTo(3);
        assertThat(config.readoutWindowInSeconds()).isEqualTo(7);
        assertThat(config.blocksBrokenThreshold()).isEqualTo(4);
        assertThat(config.debugMode()).isTrue();
        assertThat(config.readoutBlockSet()).containsExactlyInAnyOrder(
            "diamond_ore",
            "emerald_ore"
        );

        assertThat(config.targets()).hasSize(3);

        TargetConfig discord = config.targets().get(0);
        assertThat(discord).isInstanceOf(DiscordConfig.class);
        assertThat(discord.name()).isEqualTo("discord");
        assertThat(discord.enabled()).isTrue();
        assertThat(((DiscordConfig) discord).webhookUrl()).isEqualTo(
            "https://example.com/hook"
        );

        TargetConfig chat = config.targets().get(1);
        assertThat(chat).isInstanceOf(ChatConfig.class);
        assertThat(chat.name()).isEqualTo("server-chat");
        assertThat(chat.enabled()).isTrue();
        assertThat(((ChatConfig) chat).notificationSound()).isTrue();

        TargetConfig console = config.targets().get(2);
        assertThat(console).isInstanceOf(ServerConsoleConfig.class);
        assertThat(console.name()).isEqualTo("server-console");
        assertThat(console.enabled()).isFalse();
    }

    @Test
    void load_withInvalidYaml_throwsIOException(@TempDir Path tempDir)
        throws IOException {
        Path configPath = copyFixture("invalid.yaml", tempDir);

        YamlConfigManager manager = new YamlConfigManager(configPath);

        assertThatThrownBy(manager::load).isInstanceOf(IOException.class);
        assertThat(manager.get()).isNull();
    }

    @Test
    void load_whenFileDoesNotExist_writesDefaultConfigAndLoads(
        @TempDir Path tempDir
    ) throws IOException {
        Path configPath = tempDir.resolve(CONFIG_FILE_NAME);
        assertThat(configPath).doesNotExist();

        YamlConfigManager manager = new YamlConfigManager(configPath);
        manager.load();

        // The default-config resource should have been copied to the destination.
        assertThat(configPath).exists();

        ModConfig config = manager.get();
        assertThat(config).isNotNull();
        assertThat(config.configVersion()).isEqualTo(3);
        assertThat(config.debugMode()).isFalse();
        assertThat(config.readoutBlockSet()).contains(
            "diamond_ore",
            "emerald_ore",
            "ancient_debris"
        );
        assertThat(config.targets()).hasSize(3);
    }

    @Test
    void load_withValidYamlButMissingProperties_throwsNoSuchElementException(
        @TempDir Path tempDir
    ) throws IOException {
        Path configPath = copyFixture("partial.yaml", tempDir);

        YamlConfigManager manager = new YamlConfigManager(configPath);

        assertThatThrownBy(manager::load)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("readoutWindowInSeconds");
        assertThat(manager.get()).isNull();
    }
}
