package com.puhovin.intellijplugin.twm.settingsmanager;

import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSettingsManager implements SettingsManager {

    protected ToolWindowPreferenceStore state = new ToolWindowPreferenceStore();
    protected final Map<String, ToolWindowPreference> defaultPreferences = new HashMap<>();

    @Override
    public @NotNull ToolWindowPreferenceStore getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        this.state = state;
    }

    @Override
    public Map<String, ToolWindowPreference> getDefaultPreferences() {
        return defaultPreferences;
    }

    @Override
    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        state.setAllPreferences(preferences);
    }

    @Override
    public @NotNull List<ToolWindowPreference> getPreferredAvailabilities() {
        return new ArrayList<>(state.getAllPreferences().values());
    }

    @Override
    public @NotNull List<ToolWindowPreference> getDefaultAvailabilities() {
        return new ArrayList<>(defaultPreferences.values());
    }

    @Override
    public ToolWindowPreference getDefaultAvailability(String id) {
        return defaultPreferences.get(id);
    }

    @Override
    public void resetToDefaults() {
        state.setAllPreferences(defaultPreferences);
    }
}
