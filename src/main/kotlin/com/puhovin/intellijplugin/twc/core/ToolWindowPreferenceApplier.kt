package com.puhovin.intellijplugin.twc.core

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.AVAILABLE
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.UNAFFECTED
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.UNAVAILABLE

/**
 * Применяет доступность окон проекта. Несколько запросов до выполнения очереди IDE объединяются:
 * для каждого окна действует последнее значение. Изменяемое состояние используется только в EDT.
 */
@Service(Service.Level.PROJECT)
class ToolWindowPreferenceApplier(private val project: Project) {

    private var pending = linkedMapOf<String, AvailabilityPreference>()
    private val defaults = mutableMapOf<String, DefaultAvailability>()
    private var scheduled = false

    private data class DefaultAvailability(val window: ToolWindow, val available: Boolean)

    /** Ставит изменения в очередь окон IDE; повторный запрос заменяет ещё не применённый. */
    fun applyPreferences(preferences: Map<String, AvailabilityPreference>) {

        ApplicationManager.getApplication().assertIsDispatchThread()
        if (project.isDisposed || preferences.isEmpty()) return
        pending.putAll(preferences)
        if (scheduled) return
        scheduled = true
        ToolWindowManager.getInstance(project).invokeLater {
            scheduled = false
            applyPendingPreferences()
        }
    }

    /** Вычисляет и применяет настройки в одной задаче EDT; можно вызывать из фонового потока. */
    fun applyPreferences(resolvePreferences: () -> Map<String, AvailabilityPreference>) {
        if (project.isDisposed) return
        ToolWindowManager.getInstance(project).invokeLater {
            if (!project.isDisposed) {
                pending.putAll(resolvePreferences())
                applyPendingPreferences()
            }
        }
    }

    /** Забывает исходную доступность удалённого окна, чтобы новое окно с тем же ID получило свою. */
    fun forgetWindow(id: String) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        defaults.remove(id)
        pending.remove(id)
    }

    private fun applyPendingPreferences() {
        val batch = pending
        pending = linkedMapOf()
        if (project.isDisposed || batch.isEmpty()) return
        val manager = ToolWindowManager.getInstance(project)
        for ((id, preference) in batch) {
            val window = manager.getToolWindow(id) ?: continue
            val original = defaults[id]?.takeIf { it.window === window }
                ?: DefaultAvailability(window, window.isAvailable).also { defaults[id] = it }
            applyAvailability(window, preference, original.available)
        }
    }

    private fun applyAvailability(window: ToolWindow, preference: AvailabilityPreference, default: Boolean) {
        val available = when (preference) {
            AVAILABLE -> true
            UNAVAILABLE -> false
            UNAFFECTED -> default
        }
        if (window.isAvailable != available) window.setAvailable(available, null)
    }
}
