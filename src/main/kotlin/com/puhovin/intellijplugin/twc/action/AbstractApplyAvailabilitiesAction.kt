package com.puhovin.intellijplugin.twc.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference

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

        val manager = ToolWindowPreferencesManager.getInstance(project)
        manager.applyPreferences(getPreferencesToApply(manager))
    }

    /**
     * Abstract method to get a list of tool window preferences to apply.
     *
     * @param manager The tool window preferences manager
     * @return A list of preferences to apply
     */
    protected abstract fun getPreferencesToApply(manager: ToolWindowPreferencesManager): List<ToolWindowPreference>
}