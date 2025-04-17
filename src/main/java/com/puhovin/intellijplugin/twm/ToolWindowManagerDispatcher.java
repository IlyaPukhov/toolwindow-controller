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

import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.AVAILABLE;
import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAFFECTED;
import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAVAILABLE;

public final class ToolWindowManagerDispatcher {
    private static final Key<ToolWindowManagerDispatcher> KEY = Key.create("ToolWindowManagerDispatcher");
    private static final SettingsMode DEFAULT_SETTINGS_MODE = SettingsMode.GLOBAL;
    private final Lock lock = new ReentrantLock();
    private final Project project;
    private PreferredAvailabilitiesView configurationComponent;
    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode;

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
        this.settingsMode = savedMode != null ? savedMode : DEFAULT_SETTINGS_MODE;
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

    public void apply() {
        if (configurationComponent == null) return;

        lock.lock();
        try {
            List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
            Map<String, ToolWindowPreference> toSave = new HashMap<>();

            for (ToolWindowPreference pref : editedPrefs) {
                ToolWindowPreference defaultPref = getCurrentSettingsManager().getDefaultAvailabilityToolWindow(pref.getId());
                if (pref.getAvailabilityPreference() != UNAFFECTED) {
                    toSave.put(pref.getId(), pref);
                } else {
                    toSave.put(pref.getId(), defaultPref);
                }
            }

            applyPreferences(toSave);
        } finally {
            lock.unlock();
        }
    }

    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        lock.lock();
        try {
            getCurrentSettingsManager().setPreferences(preferences);
            applyCurrentPreferences();
        } finally {
            lock.unlock();
        }
    }

    private void applyCurrentPreferences() {
        List<ToolWindowPreference> prefs = getCurrentPreferences();
        ToolWindowPreferenceApplier.getInstance(project).applyPreferencesFrom(prefs);
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
                            .orElse(UNAFFECTED);
                    AvailabilityPreference edited = Optional.ofNullable(editedPref.getAvailabilityPreference())
                            .orElse(UNAFFECTED);
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
                        .getOrDefault(id, new ToolWindowPreference(id, UNAFFECTED));
                ToolWindowPreference pref = getCurrentSettingsManager().getPreferences().getOrDefault(
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
        return new ArrayList<>(getCurrentSettingsManager().getPreferences().values());
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
                AvailabilityPreference actualPref = tw.isAvailable() ? AVAILABLE : UNAVAILABLE;

                defaultPreferences.put(id, new ToolWindowPreference(id, actualPref));
            }
        }

        settingsManagerMap.values().forEach(settingsManager ->
                settingsManager.setDefaultPreferences(defaultPreferences));
    }
}