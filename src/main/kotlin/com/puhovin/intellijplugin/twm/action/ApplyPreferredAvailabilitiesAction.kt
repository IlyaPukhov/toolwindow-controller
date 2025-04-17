package com.puhovin.intellijplugin.twm.action

import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference
import org.jetbrains.annotations.NotNull

/**
 * Action to apply preferred tool window availabilities.
 */
class ApplyPreferredAvailabilitiesAction : AbstractApplyAvailabilitiesAction() {

    /**
     * Gets the preferred tool window preferences to apply.
     *
     * @param dispatcher The tool window manager dispatcher
     * @return A list of tool window preferences to apply
     */
    @NotNull
    override fun getPreferencesToApply(@NotNull dispatcher: ToolWindowManagerDispatcher): List<ToolWindowPreference> {
        return dispatcher.getCurrentAvailabilityToolWindows()
    }
}
