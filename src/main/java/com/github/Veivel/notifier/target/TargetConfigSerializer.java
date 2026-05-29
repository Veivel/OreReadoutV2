package com.github.Veivel.notifier.target;

import com.github.Veivel.logger.ModLogger;
import com.github.Veivel.notifier.target.chat.ChatConfig;
import com.github.Veivel.notifier.target.console.ServerConsoleConfig;
import com.github.Veivel.notifier.target.discord.DiscordConfig;
import java.lang.reflect.Type;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class TargetConfigSerializer
    implements TypeSerializer<TargetConfig>
{

    public static final TargetConfigSerializer INSTANCE =
        new TargetConfigSerializer();

    private final Logger logger = ModLogger.get();

    @Override
    public TargetConfig deserialize(Type type, ConfigurationNode source) {
        ConfigurationNode nameNode = source.node("name");
        try {
            String name = nameNode.get(String.class);
            switch (name) {
                case "discord":
                    return source.get(DiscordConfig.class);
                case "server-chat": // TODO: fix magic strings
                    return source.get(ChatConfig.class);
                case "server-console":
                    return source.get(ServerConsoleConfig.class);
                default:
                    logger.error("Target config with invalid name: {}", name);
                    return null;
            }
        } catch (SerializationException e) {
            logger.error("Could not serialize TargetConfig: {}", e);
            return null;
        }
    }

    @Override
    public void serialize(
        Type type,
        @Nullable TargetConfig obj,
        ConfigurationNode node
    ) throws SerializationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
            "Unimplemented method 'serialize'"
        );
    }
}
