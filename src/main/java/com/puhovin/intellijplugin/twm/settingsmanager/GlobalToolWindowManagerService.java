package com.puhovin.intellijplugin.twm.settingsmanager;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;

@Service(Service.Level.APP)
@State(name = "toolwindow-manager-global-settings", storages = @Storage("toolwindow-manager-global-settings.xml"))
public final class GlobalToolWindowManagerService implements SettingsManager {
    private ToolWindowPreferenceStore globalState = new ToolWindowPreferenceStore();

}