package com.puhovin.intellijplugin.twm.action;

import com.puhovin.intellijplugin.twm.ToolWindowManagerDispatcher;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApplyDefaultAvailabilitiesAction extends AbstractApplyAvailabilitiesAction {

    @Override
    @NotNull
    protected List<ToolWindowPreference> getPreferencesToApply(@NotNull ToolWindowManagerDispatcher dispatcher) {
        return dispatcher.getDefaultAvailabilities();
    }
}