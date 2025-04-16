package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.settingsmanager.SettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.List;
import java.util.Map;

@Service(Service.Level.APP)
public final class ToolWindowManagerDispatcher implements PersistentStateComponent<ToolWindowPreferenceStore> {

    private static ToolWindowManagerDispatcher globalInstance;
    private final SettingsManager settingsManager;
    private SettingsMode settingsMode = SettingsMode.GLOBAL;
    private final Project project;

    private ToolWindowManagerDispatcher(Project project) {
        this.project = project;
        this.settingsManager = new SettingsManager(project);
    }

    public static ToolWindowManagerDispatcher getInstance(@Nullable Project project) {
        if (project == null) {
            if (globalInstance == null) {
                synchronized (ToolWindowManagerDispatcher.class) {
                    if (globalInstance == null) {
                        globalInstance = ApplicationManager.getApplication().getService(ToolWindowManagerDispatcher.class);
                    }
                }
            }
            return globalInstance;
        } else {
            return new ToolWindowManagerDispatcher(project);
        }
    }

    private SettingsManager getActiveManager() {
        return settingsManager;
    }

    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        settingsManager.applyPreferences(preferences);
    }

    public void apply() {
        List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
        Map<String, ToolWindowPreference> newPrefs = settingsManager.mapPreferences(editedPrefs);
        settingsManager.applyPreferences(newPrefs);
        applyCurrentPreferences(newPrefs);
    }

    private void applyCurrentPreferences(Map<String, ToolWindowPreference> newPrefs) {
        // Применяем визуально в IDE
        // implement your logic here
    }

    public List<ToolWindowPreference> getPreferredAvailabilities() {
        return settingsManager.getPreferredAvailabilities();
    }

    public void resetToDefaults() {
        settingsManager.resetToDefaults();
    }

    public boolean isModified() {
        return settingsManager.isModified();
    }

    public ToolWindowPreference getAvailability(@NotNull String id) {
        return settingsManager.getAvailability(id);
    }

    public List<ToolWindowPreference> getAvailableToolWindows() {
        return settingsManager.getAvailableToolWindows();
    }

    @Override
    public @Nullable ToolWindowPreferenceStore getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        // Handle loading state if necessary
    }

    public void setSettingsMode(SettingsMode mode) {
        this.settingsMode = mode;
    }

    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    public void switchSettingsMode(SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
        settingsManager.setSettingsMode(settingsMode);
    }

    public JComponent createComponent() {
        return settingsManager.createComponent();
    }

    public void disposeUIResources() {
        settingsManager.disposeUIResources();
    }
}