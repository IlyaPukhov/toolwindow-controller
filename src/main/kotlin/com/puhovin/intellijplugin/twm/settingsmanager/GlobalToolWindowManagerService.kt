package com.puhovin.intellijplugin.twm.settingsmanager

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(name = "toolwindow-manager-global-settings", storages = [Storage("toolwindow-manager-global-settings.xml")])
class GlobalToolWindowManagerService : AbstractSettingsManager()