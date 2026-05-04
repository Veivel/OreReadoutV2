package com.github.Veivel.server;

import java.util.HashMap;
import java.util.Map;

public class PreferenceManager {

    private Map<String, Map<String, Object>> data;

    public PreferenceManager() {
        data = new HashMap<>();
    }

    public void put(String playerUuidString, String key, Object value) {
        if (!data.containsKey(playerUuidString)) {
            data.put(playerUuidString, new HashMap<String, Object>());
        }

        Map<String, Object> playerSession = data.get(playerUuidString);
        playerSession.put(key, value);
    }

    public void remove(String playerUuidString, String key) {
        if (!data.containsKey(playerUuidString)) {
            return;
        }

        Map<String, Object> playerSession = data.get(playerUuidString);
        playerSession.remove(key);
    }

    public Object get(
        String playerUuidString,
        String key,
        Object defaultValue
    ) {
        Object value = get(playerUuidString, key);
        if (value == null) {
            return defaultValue;
        }
        return (Object) value;
    }

    public Object get(String playerUuidString, String key) {
        if (!data.containsKey(playerUuidString)) {
            return null;
        }

        Map<String, Object> playerSession = data.get(playerUuidString);

        // Return value may be null
        return playerSession.get(key);
    }

    /*
     * Persists the entire session store to a file.
     * TODO: currently not implemented
     */
    public void persist() {}
}
