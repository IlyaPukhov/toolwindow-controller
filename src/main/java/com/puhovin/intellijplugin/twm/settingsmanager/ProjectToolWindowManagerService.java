package com.puhovin.intellijplugin.twm.settingsmanager;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-manager-settings", storages = @Storage("toolwindow-manager-settings.xml"))
public final class ProjectToolWindowManagerService implements SettingsManager {
    private ToolWindowPreferenceStore globalState = new ToolWindowPreferenceStore();

    public ProjectToolWindowManagerService() {
        // Инициализация глобальных настроек
    }

    @Override
    public ToolWindowPreferenceStore getState() {
        return globalState;
    }

    @Override
    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        // Логика применения изменений в глобальных настройках
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        this.globalState = state;
    }

    public void apply(Map<String, ToolWindowPreference> preferences) {
        globalState.setAllPreferences(preferences);
    }

    public boolean isModified() {
        // Логика для проверки изменений в глобальных настройках
        return false;
    }

    public void resetToDefaults() {
        // Логика сброса глобальных настроек
    }

    @Override
    public List<ToolWindowPreference> getAvailabilities() {
        return List.of();
    }
}
