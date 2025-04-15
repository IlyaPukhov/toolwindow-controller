package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(Service.Level.PROJECT)
@State(name = "ToolWindowManagerSettings", storages = {
        @Storage("ToolWindowManagerSettings.xml"),
        @Storage(value = "ToolWindowManagerGlobalSettings.xml", roamingType = RoamingType.DISABLED)
})
public final class ToolWindowManagerService implements PersistentStateComponent<ToolWindowPreferenceStore> {
    private ToolWindowPreferenceStore projectState = new ToolWindowPreferenceStore();
    private ToolWindowPreferenceStore globalState = new ToolWindowPreferenceStore();
    private boolean useGlobalSettings = true;
    private final Project project;
    private PreferredAvailabilitiesView configurationComponent;
    private final Map<String, ToolWindowPreference> defaultPreferences = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public ToolWindowManagerService(Project project) {
        this.project = project;
        initializeDefaults();
    }

    private void initializeDefaults() {
        synchronized (lock) {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            for (String id : manager.getToolWindowIds()) {
                ToolWindow toolWindow = manager.getToolWindow(id);
                if (toolWindow != null) {
                    AvailabilityPreference defaultPref = toolWindow.isAvailable()
                            ? AvailabilityPreference.AVAILABLE
                            : AvailabilityPreference.UNAVAILABLE;
                    defaultPreferences.put(id, new ToolWindowPreference(id, defaultPref));
                }
            }
        }
    }

    @Override
    public @Nullable ToolWindowPreferenceStore getState() {
        return useGlobalSettings ? globalState : projectState;
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        if (useGlobalSettings) {
            this.globalState = state;
        } else {
            this.projectState = state;
        }
        applyCurrentPreferences();
    }

    @NotNull
    public List<ToolWindowPreference> getAvailableToolWindows() {
        List<ToolWindowPreference> result = new ArrayList<>();
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        for (String id : manager.getToolWindowIds()) {
            ToolWindow tw = manager.getToolWindow(id);
            if (tw != null) {
                ToolWindowPreference activePref = getActivePreferences().getOrDefault(
                        id,
                        new ToolWindowPreference(id, AvailabilityPreference.UNAFFECTED)
                );
                result.add(activePref);
            }
        }

        result.sort(Comparator.comparing(ToolWindowPreference::id));
        return result;
    }

    public void setUseGlobalSettings(boolean useGlobal) {
        this.useGlobalSettings = useGlobal;
        applyCurrentPreferences();
    }

    public boolean isUsingGlobalSettings() {
        return useGlobalSettings;
    }

    private void applyCurrentPreferences() {
        List<ToolWindowPreference> prefs = new ArrayList<>(getActivePreferences().values());
        new ToolWindowPreferenceApplier(project).applyPreferencesFrom(prefs);
    }

    @NotNull
    public Map<String, ToolWindowPreference> getActivePreferences() {
        Map<String, ToolWindowPreference> prefs = new HashMap<>();
        if (globalState != null) {
            prefs.putAll(globalState.getAllPreferences());
        }
        if (!useGlobalSettings && projectState != null) {
            prefs.putAll(projectState.getAllPreferences());
        }
        return prefs;
    }

    @NotNull
    public List<ToolWindowPreference> getPreferredAvailabilities() {
        return new ArrayList<>(getActivePreferences().values());
    }

    @NotNull
    public List<ToolWindowPreference> getDefaultAvailabilities() {
        return new ArrayList<>(defaultPreferences.values());
    }

    @NotNull
    public List<ToolWindowPreference> getGlobalDefaultAvailabilities() {
        return globalState != null
                ? new ArrayList<>(globalState.getAllPreferences().values())
                : getDefaultAvailabilities();
    }

    public JComponent createComponent() {
        configurationComponent = new PreferredAvailabilitiesView(project);
        return configurationComponent;
    }

    public void apply() {
        List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
        Map<String, ToolWindowPreference> newPrefs = new HashMap<>();
        editedPrefs.forEach(p -> newPrefs.put(p.id(), p));

        synchronized (lock) {
            if (useGlobalSettings) {
                globalState.setAllPreferences(newPrefs);
            } else {
                projectState.setAllPreferences(newPrefs);
            }
        }

        applyCurrentPreferences();
    }

    public boolean isModified() {
        if (configurationComponent == null) {
            return false;
        }

        Map<String, AvailabilityPreference> currentPrefs = new HashMap<>();
        getActivePreferences().values().forEach(pref ->
                currentPrefs.put(pref.id(), pref.availabilityPreference())
        );

        return configurationComponent.getCurrentViewState().stream()
                .anyMatch(editedPref ->
                        !currentPrefs.getOrDefault(editedPref.id(), AvailabilityPreference.UNAFFECTED)
                                .equals(editedPref.availabilityPreference())
                );
    }

    public void reset() {
        if (configurationComponent != null) {
            configurationComponent.reset();
        }
    }

    public void disposeUIResources() {
        configurationComponent = null;
    }

    @Nullable
    public ToolWindowPreference getDefaultAvailability(@NotNull String id) {
        return defaultPreferences.get(id);
    }
}