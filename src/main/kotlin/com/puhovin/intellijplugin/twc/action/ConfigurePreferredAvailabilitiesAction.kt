package com.puhovin.intellijplugin.twc.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.puhovin.intellijplugin.twc.ui.PreferredAvailabilitiesConfigurable

/** Открывает настройки доступности окон текущего проекта. */
class ConfigurePreferredAvailabilitiesAction : DumbAwareAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ShowSettingsUtil.getInstance().editConfigurable(project, PreferredAvailabilitiesConfigurable(project))
    }
}
