package com.puhovin.intellijplugin.twm.settingsmanager;

import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface SettingsManager {

    void loadState(@NotNull ToolWindowPreferenceStore state);

    ToolWindowPreferenceStore getState();

    void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences);

    boolean isModified();

    void resetToDefaults();

    List<ToolWindowPreference> getAvailabilities();
}
