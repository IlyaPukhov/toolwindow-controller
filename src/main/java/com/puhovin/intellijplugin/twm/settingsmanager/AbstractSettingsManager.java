package com.puhovin.intellijplugin.twm.settingsmanager;

import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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
        applyPreferences(state.getPreferences());
    }

    @Override
    public Map<String, ToolWindowPreference> getDefaultPreferences() {
        return defaultPreferences;
    }

    @Override
    public void setDefaultPreferences(@NotNull Map<String, ToolWindowPreference> defaultPreferences) {
        this.defaultPreferences.putAll(defaultPreferences);
    }

    @Override
    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        this.state.setPreferences(preferences);
    }

    @Override
    public ToolWindowPreference getDefaultAvailabilityToolWindow(String id) {
        return defaultPreferences.get(id);
    }
}