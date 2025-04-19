package com.puhovin.intellijplugin.twc.model

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.io.Serializable
import org.jetbrains.annotations.NotNull

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-controller-mode", storages = [Storage("toolwindow-controller-settings.xml")])
class ToolWindowControllerSettings : PersistentStateComponent<ToolWindowControllerSettings.SettingsState> {

    private var settingsState = SettingsState()

    @Tag("settings")
    data class SettingsState(
        @Attribute("settings-mode")
        var settingsMode: SettingsMode = SettingsMode.GLOBAL
    ) : Serializable

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
    fun getSettingsMode(): SettingsMode {
        return settingsState.settingsMode
    }

    /**
     * Sets the settings mode.
     */
    fun setSettingsMode(@NotNull settingsMode: SettingsMode) {
        settingsState.settingsMode = settingsMode
    }
}
