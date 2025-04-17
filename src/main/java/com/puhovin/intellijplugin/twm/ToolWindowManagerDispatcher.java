package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowManagerSettings;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.settingsmanager.GlobalToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.ProjectToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.SettingsManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ToolWindowManagerDispatcher {
    private final Project project;
    private PreferredAvailabilitiesView configurationComponent;
    private final Lock lock = new ReentrantLock();
    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode;
    private static final Key<ToolWindowManagerDispatcher> KEY = Key.create("ToolWindowManagerDispatcher");

    public static ToolWindowManagerDispatcher getInstance(@NotNull Project project) {
        ToolWindowManagerDispatcher dispatcher = project.getUserData(KEY);
        if (dispatcher == null) {
            dispatcher = new ToolWindowManagerDispatcher(project);
            project.putUserData(KEY, dispatcher);
        }
        return dispatcher;
    }

    private ToolWindowManagerDispatcher(@NotNull Project project) {
        this.project = project;
        initializeSettingsManagerMap();
        loadSettingsMode();
    }

    private void loadSettingsMode() {
        ToolWindowManagerSettings settings = project.getService(ToolWindowManagerSettings.class);
        SettingsMode savedMode = settings.getSettingsMode();
        this.settingsMode = savedMode != null ? savedMode : SettingsMode.GLOBAL;
        switchSettingsMode(this.settingsMode);
    }

    private void initializeSettingsManagerMap() {
        settingsManagerMap.putIfAbsent(SettingsMode.GLOBAL, ApplicationManager.getApplication().getService(GlobalToolWindowManagerService.class));

        if (project != null) {
            settingsManagerMap.putIfAbsent(SettingsMode.PROJECT, project.getService(ProjectToolWindowManagerService.class));
        }
    }

    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    public void switchSettingsMode(@NotNull SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
        saveSettingsMode(settingsMode);
    }

    private void saveSettingsMode(@NotNull SettingsMode settingsMode) {
        ToolWindowManagerSettings settings = project.getService(ToolWindowManagerSettings.class);
        settings.setSettingsMode(settingsMode);
    }

    public SettingsManager getCurrentSettingsManager() {
        return settingsManagerMap.get(settingsMode);
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
        if (configurationComponent == null) return;

        lock.lock();
        try {
            List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
            Map<String, ToolWindowPreference> toSave = new HashMap<>();

            for (ToolWindowPreference pref : editedPrefs) {
                ToolWindowPreference defaultPref = getCurrentSettingsManager().getDefaultAvailabilityToolWindow(pref.getId());
                AvailabilityPreference defAvail = defaultPref == null
                        ? AvailabilityPreference.UNAFFECTED
                        : defaultPref.getAvailabilityPreference();

                if (pref.getAvailabilityPreference() != defAvail && pref.getAvailabilityPreference() != AvailabilityPreference.UNAFFECTED) {
                    toSave.put(pref.getId(), pref);
                }
            }

            getCurrentSettingsManager().applyPreferences(toSave);
            applyCurrentPreferences();
        } finally {
            lock.unlock();
        }
    }


    private void applyCurrentPreferences() {
        List<ToolWindowPreference> prefs = getCurrentPreferences();
        new ToolWindowPreferenceApplier(project, this).applyPreferencesFrom(prefs);
    }

    public boolean isModified() {
        if (configurationComponent == null) return false;

        Map<String, AvailabilityPreference> currentPrefs = new HashMap<>();
        getCurrentPreferences().forEach(pref ->
                currentPrefs.put(pref.getId(), pref.getAvailabilityPreference())
        );

        return configurationComponent.getCurrentViewState().stream()
                .anyMatch(editedPref -> {
                    AvailabilityPreference current = Optional.ofNullable(currentPrefs.get(editedPref.getId()))
                            .orElse(AvailabilityPreference.UNAFFECTED);
                    AvailabilityPreference edited = Optional.ofNullable(editedPref.getAvailabilityPreference())
                            .orElse(AvailabilityPreference.UNAFFECTED);
                    return !current.equals(edited);
                });
    }

    public List<ToolWindowPreference> getAvailableToolWindows() {
        List<ToolWindowPreference> result = new ArrayList<>();
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        for (String id : manager.getToolWindowIds()) {
            ToolWindow tw = manager.getToolWindow(id);
            if (tw != null) {
                ToolWindowPreference defaultPref = getCurrentSettingsManager().getDefaultPreferences()
                        .getOrDefault(id, new ToolWindowPreference(id, AvailabilityPreference.UNAFFECTED));
                ToolWindowPreference pref = getCurrentSettingsManager().getState().getPreferences().getOrDefault(
                        id, defaultPref);
                result.add(pref);
            }
        }

        result.sort(Comparator.comparing(ToolWindowPreference::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }

    public PreferredAvailabilitiesView getConfigurationComponent() {
        if (configurationComponent == null) {
            configurationComponent = new PreferredAvailabilitiesView(project, this);
        }
        return configurationComponent;
    }

    public void disposeUIResources() {
        configurationComponent = null;
    }

    public void reset() {
        lock.lock();
        try {
            applyCurrentPreferences();

            if (configurationComponent != null) {
                configurationComponent.reset(getAvailableToolWindows());
            }
        } finally {
            lock.unlock();
        }
    }

    public List<ToolWindowPreference> getCurrentPreferences() {
        return new ArrayList<>(getCurrentSettingsManager().getState().getPreferences().values());
    }

    public @NotNull List<ToolWindowPreference> getDefaultAvailabilityToolWindows() {
        return new ArrayList<>(getCurrentSettingsManager().getDefaultPreferences().values());
    }

    public ToolWindowPreference getDefaultAvailabilityToolWindow(@NotNull String id) {
        return getCurrentSettingsManager().getDefaultAvailabilityToolWindow(id);
    }

    public void initializeDefaultPreferences(Project project) {
        Map<String, ToolWindowPreference> defaultPreferences = new HashMap<>();
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        for (String id : manager.getToolWindowIds()) {
            ToolWindow tw = manager.getToolWindow(id);
            if (tw != null) {
                AvailabilityPreference actualPref = tw.isAvailable()
                        ? AvailabilityPreference.AVAILABLE
                        : AvailabilityPreference.UNAVAILABLE;

                defaultPreferences.put(id, new ToolWindowPreference(id, actualPref));
            }
        }

        settingsManagerMap.values().forEach(settingsManager ->
                settingsManager.setDefaultPreferences(defaultPreferences));
    }
}