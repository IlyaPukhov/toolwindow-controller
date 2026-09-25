package com.puhovin.intellijplugin.twc.settingsmanager

import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.model.ToolWindowPreferenceStore

/** Общая реализация хранения предпочтений для сервисов двух режимов. */
abstract class AbstractSettingsManager : SettingsManager {

    private var state = ToolWindowPreferenceStore()

    override fun getState(): ToolWindowPreferenceStore = state

    override fun loadState(state: ToolWindowPreferenceStore) {
        this.state = state
    }

    override fun getPreferences(): Map<String, ToolWindowPreference> = state.getPreferences()

    override fun setPreferences(preferences: Map<String, ToolWindowPreference?>) {
        state = ToolWindowPreferenceStore().apply { setPreferences(preferences) }
    }
}
