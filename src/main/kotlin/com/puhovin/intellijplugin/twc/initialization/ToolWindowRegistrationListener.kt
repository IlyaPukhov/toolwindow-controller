package com.puhovin.intellijplugin.twc.initialization

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferenceApplier
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** Применяет сохранённые настройки к новым окнам, передавая фоновые события в очередь EDT. */
class ToolWindowRegistrationListener(private val project: Project) : ToolWindowManagerListener {

    override fun toolWindowsRegistered(ids: List<String>, toolWindowManager: ToolWindowManager) {
        if (!project.isDisposed) {
            project.service<ToolWindowPreferencesManager>().applyCurrentPreferences(ids)
        }
    }

    override fun toolWindowUnregistered(id: String, toolWindow: ToolWindow) {
        onEdt {
            project.service<ToolWindowPreferenceApplier>().forgetWindow(id)
        }
    }

    private fun onEdt(action: () -> Unit) {
        if (project.isDisposed) return
        if (ApplicationManager.getApplication().isDispatchThread) {
            action()
        } else {
            ToolWindowManager.getInstance(project).invokeLater {
                if (!project.isDisposed) action()
            }
        }
    }
}
