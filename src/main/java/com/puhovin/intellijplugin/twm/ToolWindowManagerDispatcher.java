package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import com.puhovin.intellijplugin.twm.settingsmanager.GlobalToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.ProjectToolWindowManagerService;
import com.puhovin.intellijplugin.twm.settingsmanager.SettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ToolWindowManagerDispatcher implements PersistentStateComponent<ToolWindowPreferenceStore> {
    private static ToolWindowManagerDispatcher globalInstance;

    private final Project project;
    private PreferredAvailabilitiesView configurationComponent;
    private final Lock lock = new ReentrantLock();

    private final Map<SettingsMode, SettingsManager> settingsManagerMap = new EnumMap<>(SettingsMode.class);
    private SettingsMode settingsMode = SettingsMode.GLOBAL;

    private ToolWindowManagerDispatcher(Project project) {
        this.project = project;
        initializeSettingsManagerMap();
    }

    public static ToolWindowManagerDispatcher getInstance(@NotNull Project project) {
        return new ToolWindowManagerDispatcher(project);
    }

    private void initializeSettingsManagerMap() {
        settingsManagerMap.putIfAbsent(SettingsMode.GLOBAL, ApplicationManager.getApplication().getService(GlobalToolWindowManagerService.class));

        if (project != null && !project.isDefault()) {
            settingsManagerMap.putIfAbsent(SettingsMode.PROJECT, project.getService(ProjectToolWindowManagerService.class));
        }
    }

    public SettingsMode getSettingsMode() {
        return settingsMode;
    }

    public void switchSettingsMode(SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
    }

    // Получение текущего менеджера настроек в зависимости от выбранного режима
    public SettingsManager getCurrentSettingsManager() {
        return settingsManagerMap.get(settingsMode);
    }

    @Override
    public @Nullable ToolWindowPreferenceStore getState() {
        return null; // Реализуйте сохранение состояния, если требуется
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        // Загрузка состояния, если необходимо
    }

    // Применение предпочтений из переданных данных
    public void applyPreferences(@NotNull Map<String, ToolWindowPreference> preferences) {
        lock.lock();
        try {
            getCurrentSettingsManager().applyPreferences(preferences);
        } finally {
            lock.unlock();
        }
    }

    // Применение текущих предпочтений
    public void apply() {
        lock.lock();
        try {
            List<ToolWindowPreference> editedPrefs = configurationComponent.getCurrentViewState();
            Map<String, ToolWindowPreference> newPrefs = new HashMap<>();
            for (ToolWindowPreference pref : editedPrefs) {
                newPrefs.put(pref.getId(), pref);
            }

            getCurrentSettingsManager().applyPreferences(newPrefs);
            applyCurrentPreferences(newPrefs);
        } finally {
            lock.unlock();
        }
    }

    // Применение визуальных изменений в IDE
    private void applyCurrentPreferences(Map<String, ToolWindowPreference> newPrefs) {
        // Применяем визуально в IDE
        // Реализуйте вашу логику здесь
    }

    // Получение предпочтений доступных окон инструментов
    public List<ToolWindowPreference> getPreferredAvailabilities() {
        return getCurrentSettingsManager().getPreferredAvailabilities();
    }

    // Сброс предпочтений к значениям по умолчанию
    public void resetToDefaults() {
        getCurrentSettingsManager().resetToDefaults();
    }

    // Проверка, были ли изменения
    public boolean isModified() {
        return getCurrentSettingsManager().isModified();
    }

    // Получение предпочтений для конкретного окна инструмента
    public ToolWindowPreference getAvailability(@NotNull String id) {
        return getCurrentSettingsManager().getAvailability(id);
    }

    // Получение списка доступных окон инструментов
    public List<ToolWindowPreference> getAvailableToolWindows() {
        return getCurrentSettingsManager().getAvailableToolWindows();
    }

    // Создание компонента для UI
    public JComponent createComponent() {
        return getCurrentSettingsManager().createComponent();
    }

    // Освобождение ресурсов UI
    public void disposeUIResources() {
        getCurrentSettingsManager().disposeUIResources();
    }

    // Сброс всех предпочтений к дефолтным значениям
    public void resetToDefaultPreferences() {
        lock.lock();
        try {
            getCurrentSettingsManager().resetToDefaultPreferences();
        } finally {
            lock.unlock();
        }
    }

    public @NotNull List<ToolWindowPreference> getDefaultAvailabilities() {
        return null;
    }

    public ToolWindowPreference getDefaultAvailability(String id) {
        return null;
    }
}