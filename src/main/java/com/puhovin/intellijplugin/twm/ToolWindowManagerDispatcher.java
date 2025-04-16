package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import com.puhovin.intellijplugin.twm.settingsmanager.GlobalToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.ProjectToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.SettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.puhovin.intellijplugin.twm.model.SettingsMode.GLOBAL;
import static com.puhovin.intellijplugin.twm.model.SettingsMode.PROJECT;

public final class ToolWindowManagerDispatcher implements PersistentStateComponent<ToolWindowPreferenceStore> {
    private final Project project;

    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode = GLOBAL;

    public ToolWindowManagerDispatcher(Project project) {
        this.project = project;
        initializeSettingsManager();
    }

    private void initializeSettingsManager() {
        settingsManagerMap.putIfAbsent(GLOBAL, ApplicationManager.getApplication().getService(GlobalToolWindowManagerService.class));

        if (project != null && !project.isDefault()) {
            settingsManagerMap.putIfAbsent(PROJECT, project.getService(ProjectToolWindowManagerService.class));
        }
    }

    public void switchSettingsMode(SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
        initializeSettingsManager();
    }

    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    public void applyPreferences(Map<String, ToolWindowPreference> preferences) {
        settingsManagerMap.get(settingsMode).applyPreferences(preferences);
    }

    public void resetToDefaultPreferences() {
        settingsManagerMap.get(settingsMode).resetToDefaults();
    }

    public boolean isModified() {
        return settingsManagerMap.get(settingsMode).isModified();
    }

    @Override
    public @Nullable ToolWindowPreferenceStore getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore toolWindowPreferenceStore) {

    }

    @NotNull
    public List<ToolWindowPreference> getDefaultAvailabilities() {
        return settingsManagerMap.get(settingsMode).getAvailabilities();
    }

    public ToolWindowPreference getDefaultAvailability(String id) {
        return settingsManagerMap.get(settingsMode).getAvailability(id);
    }

    public @NotNull List<ToolWindowPreference> getPreferredAvailabilities() {
        return settingsManagerMap.get(settingsMode).getPreferredAvailabilities();
    }

    public JComponent createComponent() {
        return null;
    }

    public void apply() {}

    public void disposeUIResources() {}

    public List<ToolWindowPreference> getAvailableToolWindows() {
        return settingsManagerMap.get(settingsMode).getAvailableToolWindows();
    }
}