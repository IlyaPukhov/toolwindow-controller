package com.puhovin.intellijplugin.twc.core

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.UNAFFECTED
import com.puhovin.intellijplugin.twc.model.SettingsMode
import com.puhovin.intellijplugin.twc.model.ToolWindowControllerSettings
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.settingsmanager.GlobalToolWindowManagerService
import com.puhovin.intellijplugin.twc.settingsmanager.ProjectToolWindowManagerService
import com.puhovin.intellijplugin.twc.settingsmanager.SettingsManager

/**
 * Использует выбранный режим настроек проекта и отправляет предпочтения окон на применение.
 * Запоминает ID переопределённых окон, чтобы при сбросе вернуть их исходную доступность.
 */
@Service(Service.Level.PROJECT)
class ToolWindowPreferencesManager(private val project: Project) {

    private val appliedIds = mutableSetOf<String>()

    val settingsMode: SettingsMode
        get() = project.service<ToolWindowControllerSettings>().getSettingsMode()

    private fun settings(mode: SettingsMode): SettingsManager = when (mode) {
        SettingsMode.GLOBAL -> service<GlobalToolWindowManagerService>()
        SettingsMode.PROJECT -> project.service<ProjectToolWindowManagerService>()
    }

    /** Возвращает зарегистрированные окна и настройки выбранного режима для таблицы. */
    fun getAvailableToolWindows(mode: SettingsMode = settingsMode): List<ToolWindowPreference> {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val preferences = settings(mode).getPreferences()
        return ToolWindowManager.getInstance(project).toolWindowIds.sorted().map { id ->
            ToolWindowPreference(id, preferences[id]?.availabilityPreference ?: UNAFFECTED)
        }
    }

    /** Сравнивает черновик видимых окон и режим с сохранёнными настройками. */
    fun isModified(mode: SettingsMode, preferences: List<ToolWindowPreference>): Boolean {
        if (mode != settingsMode) return true
        val saved = settings(mode).getPreferences()
        return preferences.any { (saved[it.id]?.availabilityPreference ?: UNAFFECTED) != it.availabilityPreference }
    }

    /** Сохраняет настройки видимых окон, сохраняя записи окон, не зарегистрированных в этом проекте. */
    fun apply(mode: SettingsMode, preferences: List<ToolWindowPreference>) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val previous = settings(settingsMode).getPreferences()
        // В глобальном режиме здесь видны не все окна других проектов.
        val updated = settings(mode).getPreferences().toMutableMap()
        for (preference in preferences) {
            val id = preference.id ?: continue
            val availability = preference.availabilityPreference ?: UNAFFECTED
            if (availability == UNAFFECTED) updated.remove(id)
            else updated[id] = ToolWindowPreference(id, availability)
        }
        settings(mode).setPreferences(updated)
        project.service<ToolWindowControllerSettings>().setSettingsMode(mode)
        applyResolvedPreferences(appliedIds + previous.keys + updated.keys, updated)
    }

    /** Применяет сохранённые настройки; [ids] ограничивает обработку только что добавленными окнами. */
    fun applyCurrentPreferences(ids: List<String>? = null) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val saved = settings(settingsMode).getPreferences()
        // Новое окно без настройки сохраняет доступность, заданную платформой.
        applyResolvedPreferences(ids?.filter { it in saved } ?: (appliedIds + saved.keys), saved)
    }

    /** Удаляет переопределения выбранного режима и возвращает затронутые окна к исходному состоянию. */
    fun restoreDefaults() {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val current = settings(settingsMode)
        val ids = appliedIds + current.getPreferences().keys
        current.setPreferences(emptyMap())
        applyResolvedPreferences(ids, emptyMap())
    }

    private fun applyResolvedPreferences(ids: Collection<String>, preferences: Map<String, ToolWindowPreference>) {
        val resolved = ids.associateWith { preferences[it]?.availabilityPreference ?: UNAFFECTED }
        for ((id, preference) in resolved) {
            if (preference == UNAFFECTED) appliedIds.remove(id) else appliedIds.add(id)
        }
        project.service<ToolWindowPreferenceApplier>().applyPreferences(resolved)
    }

}
