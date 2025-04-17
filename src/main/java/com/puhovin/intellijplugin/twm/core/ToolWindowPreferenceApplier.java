package com.puhovin.intellijplugin.twm.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.AVAILABLE;
import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAFFECTED;

/**
 * The ToolWindowPreferenceApplier is responsible for applying tool window preferences to a project.
 * It resolves and applies the availability preferences for tool windows, ensuring that the tool window
 * visibility and availability match the specified preferences.
 *
 * <p>This class ensures that preferences are applied asynchronously on the Event Dispatch Thread (EDT)
 * to avoid blocking the UI during application.</p>
 *
 * <p>Key responsibilities of this class:</p>
 * <ul>
 *     <li>Applying preferences from a list of tool window preferences.</li>
 *     <li>Resolving preferences, including default preferences, if necessary.</li>
 *     <li>Setting the visibility and availability of tool windows based on the resolved preferences.</li>
 * </ul>
 *
 * @see ToolWindowPreference
 * @see AvailabilityPreference
 */
public class ToolWindowPreferenceApplier {

    private static final Key<ToolWindowPreferenceApplier> KEY = Key.create("ToolWindowPreferenceApplier");
    private final Project project;

    /**
     * Private constructor that initializes the applier for the given project.
     *
     * @param project The project instance for which the tool window preferences are applied.
     */
    private ToolWindowPreferenceApplier(@NotNull Project project) {
        this.project = project;
    }

    /**
     * Retrieves the singleton instance of ToolWindowPreferenceApplier for the given project.
     *
     * @param project The project instance for which the applier is created or retrieved.
     * @return An instance of ToolWindowPreferenceApplier.
     */
    public static ToolWindowPreferenceApplier getInstance(@NotNull Project project) {
        ToolWindowPreferenceApplier instance = project.getUserData(KEY);
        if (instance == null) {
            instance = new ToolWindowPreferenceApplier(project);
            project.putUserData(KEY, instance);
        }
        return instance;
    }

    /**
     * Applies a list of tool window preferences to the corresponding tool windows in the project.
     * This method resolves each preference and sets the availability of the tool windows based on the preferences.
     *
     * @param preferences A list of tool window preferences to be applied.
     */
    public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> preferences) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindowManagerDispatcher dispatcher = ToolWindowManagerDispatcher.getInstance(project);

            for (ToolWindowPreference pref : preferences) {
                ToolWindowPreference resolved = resolvePreference(pref, dispatcher);
                applyPreference(manager, resolved);
            }
        });
    }

    /**
     * Resolves the availability preference for a tool window. If the preference is UNAFFECTED, it will
     * be replaced with the default preference for that tool window.
     *
     * @param pref       The tool window preference to be resolved.
     * @param dispatcher The dispatcher used to retrieve default preferences.
     * @return The resolved tool window preference.
     */
    private ToolWindowPreference resolvePreference(@NotNull ToolWindowPreference pref, @NotNull ToolWindowManagerDispatcher dispatcher) {
        if (pref.getAvailabilityPreference() == UNAFFECTED) {
            ToolWindowPreference defaultPref = dispatcher.getDefaultAvailabilityToolWindow(pref.getId());
            if (defaultPref != null) {
                return new ToolWindowPreference(pref.getId(), defaultPref.getAvailabilityPreference());
            }
        }
        return pref;
    }

    /**
     * Applies the resolved tool window preference to the corresponding tool window.
     * The availability of the tool window is adjusted based on the resolved preference.
     *
     * @param manager The tool window manager for the project.
     * @param pref    The resolved tool window preference to be applied.
     */
    private void applyPreference(@NotNull ToolWindowManager manager, @NotNull ToolWindowPreference pref) {
        ToolWindow tw = manager.getToolWindow(pref.getId());
        if (tw == null) return;

        AvailabilityPreference preference = pref.getAvailabilityPreference();
        if (preference == UNAFFECTED) return;

        tw.setAvailable((preference == AVAILABLE), null);
    }
}