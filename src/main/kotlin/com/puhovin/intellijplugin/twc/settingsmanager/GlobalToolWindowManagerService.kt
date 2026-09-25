package com.puhovin.intellijplugin.twc.settingsmanager

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/** Предпочтения, общие для всех проектов текущей IDE. */
@Service(Service.Level.APP)
@State(name = "toolwindow-controller-global-settings", storages = [Storage("toolwindow-controller-global-settings.xml")])
class GlobalToolWindowManagerService : AbstractSettingsManager()