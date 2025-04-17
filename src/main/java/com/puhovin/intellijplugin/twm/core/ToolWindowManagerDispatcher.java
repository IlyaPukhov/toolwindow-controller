package com.puhovin.intellijplugin.twm.core;

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
import com.puhovin.intellijplugin.twm.ui.PreferredAvailabilitiesView;
import com.puhovin.intellijplugin.twm.ui.PreferredAvailabilitiesViewHolder;
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

/**
 * The ToolWindowManagerDispatcher is responsible for managing and dispatching the preferences of tool windows
 * within a given project. It handles the initialization, application, and resetting of tool window preferences.
 * It provides functionality for switching between global and project-level settings for tool windows, applying
 * changes to the preferences, and ensuring that preferences are correctly reflected when the project is opened.
 *
 * <p>This class uses a lock mechanism to ensure thread safety when applying or resetting preferences, ensuring that
 * the tool window configurations are not modified concurrently in a multi-threaded environment.</p>
 *
 * <p>Key responsibilities of this class:</p>
 * <ul>
 *     <li>Initializing and managing tool window preferences for both global and project-specific settings.</li>
 *     <li>Applying changes to the tool window preferences based on user modifications or system configurations.</li>
 *     <li>Resetting tool windows to their default states when required.</li>
 *     <li>Switching between global and project-level settings.</li>
 * </ul>
 *
 * @see ToolWindowPreference
 * @see SettingsManager
 * @see GlobalToolWindowManagerService
 * @see ProjectToolWindowManagerService
 * @see PreferredAvailabilitiesView
 */
public final class ToolWindowManagerDispatcher {

    private static final Key<ToolWindowManagerDispatcher> KEY = Key.create("ToolWindowManagerDispatcher");
    private static final SettingsMode DEFAULT_SETTINGS_MODE = SettingsMode.GLOBAL;
    private final Lock lock = new ReentrantLock();
    private final Project project;
    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode;

    /**
     * Returns the singleton instance of ToolWindowManagerDispatcher for the given project.
     *
     * @param project The project instance for which the dispatcher is created or retrieved.
     * @return An instance of ToolWindowManagerDispatcher.
     */
    public static ToolWindowManagerDispatcher getInstance(@NotNull Project project) {
        ToolWindowManagerDispatcher dispatcher = project.getUserData(KEY);
        if (dispatcher == null) {
            dispatcher = new ToolWindowManagerDispatcher(project);
            project.putUserData(KEY, dispatcher);
        }
        return dispatcher;
    }

    /**
     * Private constructor that initializes the dispatcher for the given project. This method also
     * initializes the settings manager map and loads the current settings mode.
     *
     * @param project The project instance to associate with this dispatcher.
     */
    private ToolWindowManagerDispatcher(@NotNull Project project) {
        this.project = project;
        initializeSettingsManagerMap();
        loadSettingsMode();
    }

    /**
     * Loads the current settings mode from the project settings.
     */
    private void loadSettingsMode() {
        ToolWindowManagerSettings settings = project.getService(ToolWindowManagerSettings.class);
        SettingsMode savedMode = settings.getSettingsMode();
        this.settingsMode = savedMode != null ? savedMode : DEFAULT_SETTINGS_MODE;
        switchSettingsMode(this.settingsMode);
    }

    /**
     * Initializes the settings manager map with services for global and project-level tool window management.
     */
    private void initializeSettingsManagerMap() {
        settingsManagerMap.putIfAbsent(SettingsMode.GLOBAL, ApplicationManager.getApplication().getService(GlobalToolWindowManagerService.class));

        if (project != null) {
            settingsManagerMap.putIfAbsent(SettingsMode.PROJECT, project.getService(ProjectToolWindowManagerService.class));
        }
    }

    /**
     * Applies the current preferences to the tool windows, either by saving changes or reverting to defaults.
     */
    public void apply() {
        lock.lock();
        try {
            PreferredAvailabilitiesView view = PreferredAvailabilitiesViewHolder.getInstance(project);
            List<ToolWindowPreference> editedPrefs = view.getCurrentViewState();
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

    /**
     * Checks if any preferences have been modified.
     *
     * @return true if preferences have been modified; false otherwise.
     */
    public boolean isModified() {
        PreferredAvailabilitiesView view = PreferredAvailabilitiesViewHolder.getInstance(project);

        Map<String, AvailabilityPreference> currentPrefs = new HashMap<>();
        getCurrentAvailabilityToolWindows().forEach(pref ->
                currentPrefs.put(pref.getId(), pref.getAvailabilityPreference())
        );

        return view.getCurrentViewState().stream()
                .anyMatch(editedPref -> {
                    AvailabilityPreference current = Optional.ofNullable(currentPrefs.get(editedPref.getId()))
                            .orElse(UNAFFECTED);
                    AvailabilityPreference edited = Optional.ofNullable(editedPref.getAvailabilityPreference())
                            .orElse(UNAFFECTED);
                    return !current.equals(edited);
                });
    }

    /**
     * Resets the tool windows to their default preferences.
     */
    public void reset() {
        lock.lock();
        try {
            applyCurrentPreferences();
            PreferredAvailabilitiesView view = PreferredAvailabilitiesViewHolder.getInstance(project);
            view.reset(getAvailableToolWindows());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Applies the given preferences to the current settings manager.
     *
     * @param preferences A map of tool window IDs and their corresponding preferences.
     */
    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        lock.lock();
        try {
            getCurrentSettingsManager().setPreferences(preferences);
            applyCurrentPreferences();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Applies the current preferences to the tool windows.
     */
    private void applyCurrentPreferences() {
        List<ToolWindowPreference> prefs = getCurrentAvailabilityToolWindows();
        ToolWindowPreferenceApplier.getInstance(project).applyPreferencesFrom(prefs);
    }

    /**
     * Retrieves the available tool windows based on their current preferences.
     *
     * @return A list of available tool window preferences.
     */
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

    /**
     * Retrieves the current settings mode (global or project-specific).
     *
     * @return The current settings mode.
     */
    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    /**
     * Switches to a different settings mode (global or project-specific).
     *
     * @param settingsMode The new settings mode.
     */
    public void switchSettingsMode(@NotNull SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
        saveSettingsMode(settingsMode);
    }

    /**
     * Saves the current settings mode to the project settings.
     *
     * @param settingsMode The settings mode to save.
     */
    private void saveSettingsMode(@NotNull SettingsMode settingsMode) {
        ToolWindowManagerSettings settings = project.getService(ToolWindowManagerSettings.class);
        settings.setSettingsMode(settingsMode);
    }

    /**
     * Retrieves the settings manager corresponding to the current settings mode.
     *
     * @return The current settings manager.
     */
    public SettingsManager getCurrentSettingsManager() {
        return settingsManagerMap.get(settingsMode);
    }

    /**
     * Retrieves the current preferences for all tool windows.
     *
     * @return A list of tool window preferences.
     */
    public List<ToolWindowPreference> getCurrentAvailabilityToolWindows() {
        return new ArrayList<>(getCurrentSettingsManager().getPreferences().values());
    }

    /**
     * Retrieves the default preferences for all tool windows.
     *
     * @return A list of tool window preferences with default settings.
     */
    public @NotNull List<ToolWindowPreference> getDefaultAvailabilityToolWindows() {
        return new ArrayList<>(getCurrentSettingsManager().getDefaultPreferences().values());
    }

    /**
     * Retrieves the default preference for a specific tool window.
     *
     * @param id The ID of the tool window.
     * @return The default preference for the specified tool window.
     */
    public ToolWindowPreference getDefaultAvailabilityToolWindow(@NotNull String id) {
        return getCurrentSettingsManager().getDefaultAvailabilityToolWindow(id);
    }

    /**
     * Initializes the default preferences for tool windows based on their current availability.
     *
     * @param project The project instance for which the preferences are initialized.
     */
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