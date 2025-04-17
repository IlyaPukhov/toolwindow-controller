package com.puhovin.intellijplugin.twm.initialization

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher

/**
 * This class implements the [ProjectActivity] interface and is responsible for
 * resetting tool windows to their default visibility settings when the project is opened.
 * It executes the reset operation after the project is fully opened, ensuring that the
 * tool window preferences are restored to their defaults.
 *
 * The [ToolWindowManagerDispatcher] is used to initialize the default tool window preferences
 * and apply them by invoking the [ToolWindowManagerDispatcher.reset] method.
 *
 * This activity is triggered automatically when the project is started and does not require
 * any user interaction.
 *
 * @see ToolWindowManagerDispatcher
 */
class ApplyToolWindowsPreferencesOnStartup : ProjectActivity {

    /**
     * Executes the reset operation to restore the default visibility settings for tool windows.
     * This method is called automatically when the project is opened.
     *
     * @param project The project that is being opened.
     */
    override suspend fun execute(project: Project) {
        val dispatcher = ToolWindowManagerDispatcher.getInstance(project)
        ApplicationManager.getApplication().invokeAndWait {
            dispatcher.initializeDefaultPreferences(project)
        }
        dispatcher.applyPreferences(dispatcher.getCurrentAvailabilityToolWindows())
    }
}