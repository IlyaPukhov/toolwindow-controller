package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ToolWindowManagerActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent e) {
        final Project project = (Project) e.getDataContext().getData(CommonDataKeys.PROJECT.getName());

        e.getPresentation().setEnabledAndVisible(project != null && !project.isDefault());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }
}
