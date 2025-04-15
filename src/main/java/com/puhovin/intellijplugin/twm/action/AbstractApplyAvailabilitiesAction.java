package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import com.puhovin.intellijplugin.twm.ToolWindowPreferenceApplier;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractApplyAvailabilitiesAction extends AnAction {

    @NotNull
    protected abstract List<ToolWindowPreference> getPreferencesToApply(@NotNull final ToolWindowManagerService toolWindowManagerProjectComponent);

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = (Project) e.getDataContext().getData(CommonDataKeys.PROJECT.getName());

        if (project != null && !project.isDefault()) {
            final ToolWindowManagerService component = project.getService(ToolWindowManagerService.class);
            final List<ToolWindowPreference> toolWindowPreferences = getPreferencesToApply(component);
            final ToolWindowPreferenceApplier toolWindowPreferenceApplier = new ToolWindowPreferenceApplier(project);

            toolWindowPreferenceApplier.applyPreferencesFrom(toolWindowPreferences);
        }
    }
}
