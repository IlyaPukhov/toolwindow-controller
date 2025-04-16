package com.puhovin.intellijplugin.twm.settingsmanager;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-manager-settings", storages = @Storage("toolwindow-manager-settings.xml"))
public final class ProjectToolWindowManagerService implements SettingsManager {
    private ToolWindowPreferenceStore globalState = new ToolWindowPreferenceStore();

}