package com.puhovin.intellijplugin.twm.settingsmanager

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-manager-settings", storages = [Storage("toolwindow-manager-settings.xml")])
class ProjectToolWindowManagerService : AbstractSettingsManager()