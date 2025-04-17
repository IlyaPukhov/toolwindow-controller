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
public final class ToolWindowManagerSettings implements PersistentStateComponent<ToolWindowManagerSettings.State> {
    private State state = new State();

    @Tag("settings")
    public static class State {
        @Attribute("settings-mode")
        private SettingsMode settingsMode = SettingsMode.GLOBAL;
    }

    @NotNull
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public SettingsMode getSettingsMode() {
        return state.settingsMode;
    }

    public void setSettingsMode(@NotNull SettingsMode settingsMode) {
        this.state.settingsMode = settingsMode;
    }
}
