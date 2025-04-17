package com.puhovin.intellijplugin.twm.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-manager-mode", storages = @Storage("toolwindow-manager-settings.xml"))
public final class ToolWindowManagerSettings implements PersistentStateComponent<ToolWindowManagerSettings.SettingsState> {
    private SettingsState settingsState = new SettingsState();

    @Tag("settings")
    public static class SettingsState {
        @Attribute("settings-mode")
        private SettingsMode settingsMode;
    }

    @NotNull
    @Override
    public ToolWindowManagerSettings.SettingsState getState() {
        return settingsState;
    }

    @Override
    public void loadState(@NotNull ToolWindowManagerSettings.SettingsState settingsState) {
        this.settingsState = settingsState;
    }

    public SettingsMode getSettingsMode() {
        return settingsState.settingsMode;
    }

    public void setSettingsMode(@NotNull SettingsMode settingsMode) {
        this.settingsState.settingsMode = settingsMode;
    }
}
