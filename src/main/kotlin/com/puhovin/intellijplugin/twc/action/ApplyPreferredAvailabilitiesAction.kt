package com.puhovin.intellijplugin.twc.action

import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import org.jetbrains.annotations.NotNull

/**
 * Action to apply preferred tool window availabilities.
 */
class ApplyPreferredAvailabilitiesAction : AbstractApplyAvailabilitiesAction() {

    /**
     * Gets the preferred tool window preferences to apply.
     *
     * @param manager The tool window preferences manager
     * @return A list of tool window preferences to apply
     */
    @NotNull
    override fun getPreferencesToApply(@NotNull manager: ToolWindowPreferencesManager): List<ToolWindowPreference> {
        return manager.getCurrentAvailabilityToolWindows()
    }
}
