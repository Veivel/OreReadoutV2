package com.github.Veivel.store;

import java.util.HashMap;
import java.util.Map;

public class PlayerConfigStore {

    private static Map<String, Boolean> chatReadoutEnabledByPlayer =
        new HashMap<>();

    public static Map<String, Boolean> getChatReadoutEnabledByPlayer() {
        return chatReadoutEnabledByPlayer;
    }
}
