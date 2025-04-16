package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import com.puhovin.intellijplugin.twm.settingsmanager.GlobalToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.ProjectToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.SettingsManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ToolWindowManagerDispatcher implements PersistentStateComponent<ToolWindowPreferenceStore> {
    private final Project project;
    private PreferredAvailabilitiesView configurationComponent;
    private final Lock lock = new ReentrantLock();

    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode = SettingsMode.GLOBAL;

    private ToolWindowManagerDispatcher(Project project) {
        this.project = project;
        initializeSettingsManagerMap();
    }

    public static ToolWindowManagerDispatcher getInstance(@NotNull Project project) {
        return new ToolWindowManagerDispatcher(project);
    }

    private void initializeSettingsManagerMap() {
        settingsManagerMap.putIfAbsent(SettingsMode.GLOBAL, ApplicationManager.getApplication().getService(GlobalToolWindowManagerService.class));

        if (project != null && !project.isDefault()) {
            settingsManagerMap.putIfAbsent(SettingsMode.PROJECT, project.getService(ProjectToolWindowManagerService.class));
        }
    }

    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    public void switchSettingsMode(SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
    }

    public SettingsManager getCurrentSettingsManager() {
        return settingsManagerMap.get(settingsMode);
    }

    @Override
    public ToolWindowPreferenceStore getState() {
        return getCurrentSettingsManager().getState();
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        applyCurrentPreferences();
    }

    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        lock.lock();
        try {
            getCurrentSettingsManager().applyPreferences(preferences);
        } finally {
            lock.unlock();
        }
    }

    public void apply() {
        lock.lock();
        try {
            List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
            Map<String, ToolWindowPreference> newPrefs = new HashMap<>();
            for (ToolWindowPreference pref : editedPrefs) {
                newPrefs.put(pref.getId(), pref);
            }

            getCurrentSettingsManager().applyPreferences(newPrefs);
            applyCurrentPreferences();
        } finally {
            lock.unlock();
        }
    }

    private void applyCurrentPreferences() {
        List<ToolWindowPreference> prefs = new ArrayList<>(getCurrentSettingsManager().getState().getAllPreferences().values());
        new ToolWindowPreferenceApplier(project).applyPreferencesFrom(prefs);
    }

    public List<ToolWindowPreference> getPreferredAvailabilities() {
        return getCurrentSettingsManager().getPreferredAvailabilities();
    }

    public boolean isModified() {
        if (configurationComponent == null) return false;

        Map<String, AvailabilityPreference> currentPrefs = new HashMap<>();
        getCurrentSettingsManager().getState().getAllPreferences().values().forEach(pref ->
                currentPrefs.put(pref.getId(), pref.getAvailabilityPreference())
        );

        return configurationComponent.getCurrentViewState().stream()
                .anyMatch(editedPref -> {
                    AvailabilityPreference current = Optional.ofNullable(currentPrefs.get(editedPref.getId())).orElse(AvailabilityPreference.UNAFFECTED);
                    AvailabilityPreference edited = Optional.ofNullable(editedPref.getAvailabilityPreference()).orElse(AvailabilityPreference.UNAFFECTED);
                    return !current.equals(edited);
                });
    }

    public List<ToolWindowPreference> getAvailableToolWindows() {
        return getCurrentSettingsManager().getAvailableToolWindows();
    }

    public JComponent createComponent() {
        configurationComponent = new PreferredAvailabilitiesView(project, this);
        return configurationComponent;
    }

    public void disposeUIResources() {
        configurationComponent = null;
    }

    public void resetToDefaultPreferences() {
        lock.lock();
        try {
            getCurrentSettingsManager().resetToDefaults();
            applyCurrentPreferences();
            if (configurationComponent != null) {
                configurationComponent.reset(getDefaultAvailabilities());
            }
        } finally {
            lock.unlock();
        }
    }

    public @NotNull List<ToolWindowPreference> getDefaultAvailabilities() {
        return getCurrentSettingsManager().getDefaultAvailabilities();
    }

    public ToolWindowPreference getDefaultAvailability(String id) {
        return getCurrentSettingsManager().getDefaultAvailability(id);
    }
}