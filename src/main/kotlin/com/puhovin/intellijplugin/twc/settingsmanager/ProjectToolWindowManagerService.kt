package com.puhovin.intellijplugin.twc.settingsmanager

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.PROJECT)
@State(name = "toolwindow-controller-settings", storages = [Storage("toolwindow-controller-settings.xml")])
class ProjectToolWindowManagerService : AbstractSettingsManager()