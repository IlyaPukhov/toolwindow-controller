package com.puhovin.intellijplugin.twc.initialization

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferenceApplier
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** Применяет сохранённые настройки к окнам, зарегистрированным после открытия проекта. */
class ToolWindowRegistrationListener(private val project: Project) : ToolWindowManagerListener {

    override fun toolWindowsRegistered(ids: List<String>, toolWindowManager: ToolWindowManager) {
        if (!project.isDisposed) project.service<ToolWindowPreferencesManager>().applyCurrentPreferences(ids)
    }

    override fun toolWindowUnregistered(id: String, toolWindow: ToolWindow) {
        if (!project.isDisposed) project.service<ToolWindowPreferenceApplier>().forgetWindow(id)
    }
}
