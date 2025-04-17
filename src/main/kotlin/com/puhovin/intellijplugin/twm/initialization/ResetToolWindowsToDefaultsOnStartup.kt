package com.puhovin.intellijplugin.twm.initialization

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher

/**
 * Resets tool windows to their default visibility settings when the project is opened.
 * This will be invoked after the project is initialized.
 */
class ResetToolWindowsToDefaultsOnStartup : ProjectActivity {

    /**
     * Executes the reset operation to restore the default visibility settings for tool windows.
     * This method is called automatically when the project is fully opened.
     *
     * @param project The project that is being opened.
     */
    override suspend fun execute(project: Project) {
        val dispatcher = ToolWindowManagerDispatcher.getInstance(project)
        ApplicationManager.getApplication().invokeLater {
            dispatcher.initializeDefaultPreferences(project)
            dispatcher.reset()
        }
    }
}