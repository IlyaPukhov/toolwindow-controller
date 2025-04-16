package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ToolWindowManagerActionGroup extends DefaultActionGroup {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();

        e.getPresentation().setEnabledAndVisible(project != null && !project.isDefault());
    }
}
