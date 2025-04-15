package com.puhovin.intellijplugin.twm.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ToolWindowPreferenceStore implements Serializable {
    private Map<String, ToolWindowPreference> preferences = new HashMap<>();

    @NotNull
    public Map<String, ToolWindowPreference> getAllPreferences() {
        return new HashMap<>(preferences);
    }

    public void setAllPreferences(@NotNull Map<String, ToolWindowPreference> prefs) {
        this.preferences = new HashMap<>(prefs);
    }
}
