package com.puhovin.intellijplugin.twc.action

import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager

/** Повторно применяет сохранённые предпочтения к окнам проекта. */
class ApplyPreferredAvailabilitiesAction : AbstractApplyAvailabilitiesAction() {

    override fun apply(manager: ToolWindowPreferencesManager) = manager.applyCurrentPreferences()
}
