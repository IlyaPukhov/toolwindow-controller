package com.puhovin.intellijplugin.twc.settingsmanager

import com.intellij.openapi.components.PersistentStateComponent
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.model.ToolWindowPreferenceStore

/** Доступ к сохранённым предпочтениям глобального или проектного режима. */
interface SettingsManager : PersistentStateComponent<ToolWindowPreferenceStore> {
    override fun getState(): ToolWindowPreferenceStore
    fun getPreferences(): Map<String, ToolWindowPreference>
    fun setPreferences(preferences: Map<String, ToolWindowPreference?>)
}
