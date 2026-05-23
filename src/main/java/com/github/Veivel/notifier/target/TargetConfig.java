package com.github.Veivel.notifier.target;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.Veivel.notifier.target.chat.ChatConfig;
import com.github.Veivel.notifier.target.console.ServerConsoleConfig;
import com.github.Veivel.notifier.target.discord.DiscordConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name", visible = true)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = DiscordConfig.class, name = "discord"),
        @JsonSubTypes.Type(value = ChatConfig.class, name = "server-chat"),
        @JsonSubTypes.Type(
            value = ServerConsoleConfig.class,
            name = "server-console"
        ),
    }
)
public interface TargetConfig {
    String name();
    boolean enabled();
}
