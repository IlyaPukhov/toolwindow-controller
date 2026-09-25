package com.puhovin.intellijplugin.twc.action

import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** Удаляет переопределения доступности окон выбранного режима. */
class ApplyDefaultAvailabilitiesAction : AbstractApplyAvailabilitiesAction() {

    override fun apply(manager: ToolWindowPreferencesManager) = manager.restoreDefaults()
}
