package com.puhovin.intellijplugin.twm.settingsmanager;

import com.intellij.openapi.components.PersistentStateComponent;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface SettingsManager extends PersistentStateComponent<ToolWindowPreferenceStore> {

    @Override
    @NotNull ToolWindowPreferenceStore getState();

    @Override
    void loadState(@NotNull ToolWindowPreferenceStore state);

    void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences);

    Map<String, ToolWindowPreference> getDefaultPreferences();

    @NotNull List<ToolWindowPreference> getPreferredAvailabilities();

    @NotNull List<ToolWindowPreference> getDefaultAvailabilities();

    ToolWindowPreference getDefaultAvailability(String id);

    void resetToDefaults();
}