package com.puhovin.intellijplugin.twm.action;

import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApplyDefaultAvailabilitiesAction extends AbstractApplyAvailabilitiesAction {

    @Override
    @NotNull
    protected List<ToolWindowPreference> getPreferencesToApply(@NotNull ToolWindowManagerService service) {
        return service.isUsingGlobalSettings()
                ? service.getGlobalDefaultAvailabilities()
                : service.getDefaultAvailabilities();
    }
}