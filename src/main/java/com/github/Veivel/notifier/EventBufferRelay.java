package com.github.Veivel.notifier;

import com.github.Veivel.event.MixinEvent;

public final class EventBufferRelay {
  private static EventBuffer instance;

  public static void setInstance(EventBuffer instance) {
    EventBufferRelay.instance = instance;
  }

  public static EventBuffer getInstance() {
    return instance;
  }

  public static void flush() {
    getInstance().flush();
  }

  public static void checkAndBuffer(MixinEvent mixinEvent) {
    getInstance().checkAndBuffer(mixinEvent);
  }
}
