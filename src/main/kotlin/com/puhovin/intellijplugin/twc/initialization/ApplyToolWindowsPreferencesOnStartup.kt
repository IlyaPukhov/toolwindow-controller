package com.puhovin.intellijplugin.twc.initialization

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** При открытии проекта применяет сохранённую доступность уже зарегистрированных окон в EDT. */
class ApplyToolWindowsPreferencesOnStartup : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (!project.isDisposed) project.service<ToolWindowPreferencesManager>().applyCurrentPreferences()
    }
}
