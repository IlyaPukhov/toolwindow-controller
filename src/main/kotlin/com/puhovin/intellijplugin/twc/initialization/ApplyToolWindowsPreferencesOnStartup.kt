package com.puhovin.intellijplugin.twc.initialization

import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class implements the [ProjectActivity] interface and is responsible for
 * resetting tool windows to their default visibility settings when the project is opened.
 * It executes the reset operation after the project is fully opened, ensuring that the
 * tool window preferences are restored to their defaults.
 *
 * The [ToolWindowPreferencesManager] is used to initialize the default tool window preferences.
 *
 * This activity is triggered automatically when the project is started and does not require
 * any user interaction.
 *
 * @see ToolWindowPreferencesManager
 */
class ApplyToolWindowsPreferencesOnStartup : ProjectActivity {

    /**
     * Executes the reset operation to restore the default visibility settings for tool windows.
     * This method is called automatically when the project is opened.
     *
     * @param project The project that is being opened.
     */
    override suspend fun execute(project: Project) {
        val manager = ToolWindowPreferencesManager.getInstance(project)

        withContext(Dispatchers.EDT) {
            manager.initializeDefaultPreferences(project)
            manager.reset()
        }
    }
}