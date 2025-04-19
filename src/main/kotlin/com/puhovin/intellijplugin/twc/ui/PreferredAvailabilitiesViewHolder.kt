package com.puhovin.intellijplugin.twc.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import org.jetbrains.annotations.NotNull

/**
 * A singleton utility class for managing the lifecycle of the [PreferredAvailabilitiesView].
 * It ensures that only one instance of [PreferredAvailabilitiesView] exists per project and provides
 * methods to retrieve and dispose of the view instance.
 */
object PreferredAvailabilitiesViewHolder {
    private val KEY = Key.create<PreferredAvailabilitiesView>("PreferredAvailabilitiesView")

    /**
     * Retrieves the [PreferredAvailabilitiesView] instance associated with the given project.
     * If the view doesn't exist, it creates and stores a new instance.
     *
     * @param project The project for which the view instance is retrieved.
     * @return The [PreferredAvailabilitiesView] instance for the given project.
     */
    @NotNull
    fun getInstance(@NotNull project: Project): PreferredAvailabilitiesView {
        var view = project.getUserData(KEY)
        if (view == null) {
            view = PreferredAvailabilitiesView(project, ToolWindowPreferencesManager.getInstance(project))
            project.putUserData(KEY, view)
        }
        return view
    }

    /**
     * Disposes the [PreferredAvailabilitiesView] instance for the given project,
     * allowing for cleanup of any resources associated with it.
     *
     * @param project The project for which the view instance is disposed.
     */
    fun dispose(@NotNull project: Project) {
        project.putUserData(KEY, null)
    }
}