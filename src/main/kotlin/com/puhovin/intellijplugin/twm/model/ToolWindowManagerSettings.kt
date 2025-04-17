package com.puhovin.intellijplugin.twm.model

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import org.jetbrains.annotations.NotNull

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-manager-mode", storages = [Storage("toolwindow-manager-settings.xml")])
class ToolWindowManagerSettings : PersistentStateComponent<ToolWindowManagerSettings.SettingsState> {

    private var settingsState = SettingsState()

    @Tag("settings")
    data class SettingsState(
        @Attribute("settings-mode")
        var settingsMode: SettingsMode? = null
    )

    /**
     * Gets the current state of the settings.
     */
    @NotNull
    override fun getState(): SettingsState {
        return settingsState
    }

    /**
     * Loads the state from the XML file.
     */
    override fun loadState(@NotNull state: SettingsState) {
        this.settingsState = state
    }

    /**
     * Gets the current settings mode.
     */
    fun getSettingsMode(): SettingsMode? {
        return settingsState.settingsMode
    }

    /**
     * Sets the settings mode.
     */
    fun setSettingsMode(@NotNull settingsMode: SettingsMode) {
        settingsState.settingsMode = settingsMode
    }
}
