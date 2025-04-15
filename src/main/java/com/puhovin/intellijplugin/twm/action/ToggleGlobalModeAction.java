package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;

public class ToggleGlobalModeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ToolWindowManagerService service = project.getService(ToolWindowManagerService.class);
            service.setUseGlobalSettings(!service.isUsingGlobalSettings());
        }
    }
}
