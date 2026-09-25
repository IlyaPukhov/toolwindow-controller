package com.puhovin.intellijplugin.twc.initialization

import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** При открытии проекта применяет сохранённую доступность уже зарегистрированных окон в EDT. */
class ApplyToolWindowsPreferencesOnStartup : ProjectActivity {

    override suspend fun execute(project: Project) {
        withContext(Dispatchers.EDT) {
            if (!project.isDisposed) project.service<ToolWindowPreferencesManager>().applyCurrentPreferences()
        }
    }
}
