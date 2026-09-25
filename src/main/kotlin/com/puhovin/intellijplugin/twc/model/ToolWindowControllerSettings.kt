package com.puhovin.intellijplugin.twc.model

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.io.Serializable

/** Сохраняет выбранный для проекта режим: глобальные или проектные предпочтения. */
@Service(Service.Level.PROJECT)
@State(name = "toolwindow-controller-mode", storages = [Storage("toolwindow-controller-settings.xml")])
class ToolWindowControllerSettings : PersistentStateComponent<ToolWindowControllerSettings.SettingsState> {

    private var settingsState = SettingsState()

    /** Формат сохраняемого в XML режима настроек. */
    @Tag("settings")
    data class SettingsState(
        @Attribute("settings-mode")
        var settingsMode: SettingsMode = SettingsMode.GLOBAL
    ) : Serializable

    override fun getState(): SettingsState {
        return settingsState
    }

    override fun loadState(state: SettingsState) {
        this.settingsState = state
    }

    /** Возвращает выбранный для проекта режим. */
    fun getSettingsMode(): SettingsMode {
        return settingsState.settingsMode
    }

    /** Сохраняет выбранный для проекта режим. */
    fun setSettingsMode(settingsMode: SettingsMode) {
        settingsState.settingsMode = settingsMode
    }
}
