package com.puhovin.intellijplugin.twm.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference

/**
 * Abstract base action for applying tool window preferences.
 */
abstract class AbstractApplyAvailabilitiesAction : AnAction() {

    /**
     * Executes the action to apply the tool window preferences.
     *
     * @param e The action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val dispatcher = ToolWindowManagerDispatcher.getInstance(project)
        dispatcher.applyPreferences(getPreferencesToApply(dispatcher))
    }

    /**
     * Abstract method to get a list of tool window preferences to apply.
     *
     * @param dispatcher The tool window manager dispatcher
     * @return A list of preferences to apply
     */
    protected abstract fun getPreferencesToApply(dispatcher: ToolWindowManagerDispatcher): List<ToolWindowPreference>
}