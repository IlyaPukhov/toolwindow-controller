package com.puhovin.intellijplugin.twc.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.components.service
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** Общая обработка действий, меняющих доступность окон текущего проекта. */
abstract class AbstractApplyAvailabilitiesAction : DumbAwareAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        apply(project.service<ToolWindowPreferencesManager>())
    }

    protected abstract fun apply(manager: ToolWindowPreferencesManager)
}
