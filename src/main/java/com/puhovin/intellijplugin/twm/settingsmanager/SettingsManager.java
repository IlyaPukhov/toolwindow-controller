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

    void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences);

    @NotNull List<ToolWindowPreference> getPreferredAvailabilities();

    @NotNull List<ToolWindowPreference> getDefaultAvailabilities();

    ToolWindowPreference getDefaultAvailability(String id);

    List<ToolWindowPreference> getAvailableToolWindows();

    void resetToDefaults();
}