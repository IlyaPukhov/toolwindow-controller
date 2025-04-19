package com.puhovin.intellijplugin.twc.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project

/**
 * Action group for controlling tool window actions.
 */
class ToolWindowControllerActionGroup : DefaultActionGroup() {

    /**
     * Gets the action update thread.
     *
     * @return The action update thread to be used for this action
     */
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    /**
     * Updates the action visibility and enablement based on the project state.
     *
     * @param e The action event containing the project context
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}
