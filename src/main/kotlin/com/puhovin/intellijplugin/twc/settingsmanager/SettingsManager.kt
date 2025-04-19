package com.puhovin.intellijplugin.twc.settingsmanager

import com.intellij.openapi.components.PersistentStateComponent
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.model.ToolWindowPreferenceStore

interface SettingsManager : PersistentStateComponent<ToolWindowPreferenceStore> {

    override fun getState(): ToolWindowPreferenceStore

    override fun loadState(state: ToolWindowPreferenceStore)

    fun getPreferences(): Map<String, ToolWindowPreference>

    fun setPreferences(preferences: Map<String, ToolWindowPreference?>)

    fun getDefaultPreferences(): Map<String, ToolWindowPreference>

    fun setDefaultPreferences(preferences: Map<String, ToolWindowPreference>)

    fun getDefaultAvailabilityToolWindow(id: String?): ToolWindowPreference?
}