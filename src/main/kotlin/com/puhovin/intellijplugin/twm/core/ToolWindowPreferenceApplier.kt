package com.puhovin.intellijplugin.twm.core

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindowManager
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference.AVAILABLE
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAFFECTED
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference
import org.jetbrains.annotations.NotNull

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
class ToolWindowPreferenceApplier private constructor(@NotNull val project: Project) {

    companion object {
        private val KEY = Key.create<ToolWindowPreferenceApplier>("ToolWindowPreferenceApplier")

        /**
         * Retrieves the singleton instance of ToolWindowPreferenceApplier for the given project.
         *
         * @param project The project instance for which the applier is created or retrieved.
         * @return An instance of ToolWindowPreferenceApplier.
         */
        fun getInstance(@NotNull project: Project): ToolWindowPreferenceApplier {
            var instance = project.getUserData(KEY)
            if (instance == null) {
                instance = ToolWindowPreferenceApplier(project)
                project.putUserData(KEY, instance)
            }
            return instance
        }
    }

    /**
     * Applies a list of tool window preferences to the corresponding tool windows in the project.
     * This method resolves each preference and sets the availability of the tool windows based on the preferences.
     *
     * @param preferences A list of tool window preferences to be applied.
     */
    fun applyPreferencesFrom(@NotNull preferences: List<ToolWindowPreference>) {
        ApplicationManager.getApplication().invokeLater {
            val manager = ToolWindowManager.getInstance(project)
            val dispatcher = ToolWindowManagerDispatcher.getInstance(project)

            for (pref in preferences) {
                val resolved = resolvePreference(pref, dispatcher)
                applyPreference(manager, resolved)
            }
        }
    }

    /**
     * Resolves the availability preference for a tool window. If the preference is UNAFFECTED, it will
     * be replaced with the default preference for that tool window.
     *
     * @param pref The tool window preference to be resolved.
     * @param dispatcher The dispatcher used to retrieve default preferences.
     * @return The resolved tool window preference.
     */
    private fun resolvePreference(
        @NotNull pref: ToolWindowPreference?,
        @NotNull dispatcher: ToolWindowManagerDispatcher
    ): ToolWindowPreference {
        if (pref == null || pref.availabilityPreference == UNAFFECTED) {
            val defaultPref = dispatcher.getDefaultAvailabilityToolWindow(pref?.id)
            if (defaultPref != null) {
                return ToolWindowPreference(pref?.id ?: "", defaultPref.availabilityPreference)
            }
        }
        // Return the original preference if it's not UNAFFECTED or pref is null
        return pref ?: ToolWindowPreference("", UNAFFECTED)
    }

    /**
     * Applies the resolved tool window preference to the corresponding tool window.
     * The availability of the tool window is adjusted based on the resolved preference.
     *
     * @param manager The tool window manager for the project.
     * @param pref The resolved tool window preference to be applied.
     */
    private fun applyPreference(@NotNull manager: ToolWindowManager, @NotNull pref: ToolWindowPreference) {
        val tw = manager.getToolWindow(pref.id) ?: return
        val preference = pref.availabilityPreference
        if (preference == UNAFFECTED) return
        tw.setAvailable(preference == AVAILABLE, null)
    }
}