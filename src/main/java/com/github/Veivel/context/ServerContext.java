package com.github.Veivel.context;

import net.minecraft.server.MinecraftServer;

/**
 * Holds the active {@link MinecraftServer} instance for this mod.
 *
 * <p>Populated by {@code ServerLifecycleEvents.SERVER_STARTED} and cleared by
 * {@code ServerLifecycleEvents.SERVER_STOPPED}.</p>
 */
public final class ServerContext {
  private static volatile MinecraftServer server;

  private ServerContext() {}

  public static void set(MinecraftServer server) {
    ServerContext.server = server;
  }

  public static void clear() {
    ServerContext.server = null;
  }

  public static MinecraftServer get() {
    return server;
  }
}
