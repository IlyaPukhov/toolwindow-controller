package com.puhovin.intellijplugin.twm.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import org.jetbrains.annotations.NotNull;

public class ToggleModeAction extends ToggleAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        return project != null &&
               project.getService(ToolWindowManagerService.class).isUsingGlobalSettings();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        Project project = e.getProject();
        if (project != null) {
            ToolWindowManagerService service = project.getService(ToolWindowManagerService.class);
            service.setUseGlobalSettings(state);
            updatePresentation(e.getPresentation(), state);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Project project = e.getProject();
        boolean enabled = project != null && !project.isDefault();

        if (enabled) {
            updatePresentation(e.getPresentation(), isSelected(e));
        }
    }

    private void updatePresentation(@NotNull Presentation presentation, boolean isGlobal) {
        if (isGlobal) {
            presentation.setText("Global Mode (Switch to Project)");
            presentation.setIcon(AllIcons.Javaee.WebModuleGroup);
        } else {
            presentation.setText("Project Mode (Switch to Global)");
            presentation.setIcon(AllIcons.Nodes.Module);
        }
    }
}
