package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigurePreferredAvailabilitiesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = (Project) e.getDataContext().getData(CommonDataKeys.PROJECT.getName());

        if (project != null && !project.isDefault()) {
            final ToolWindowManagerService projectComponent =
                    project.getService(ToolWindowManagerService.class);

            ShowSettingsUtil.getInstance().editConfigurable(project, new Configurable() {

                @Override
                @Nls
                public String getDisplayName() {
                    return ToolWindowManagerBundle.message("configurable.display.name");
                }

                @Override
                @Nullable
                @NonNls
                public String getHelpTopic() {
                    return null;
                }

                @Override
                public JComponent createComponent() {
                    return projectComponent.createComponent();
                }

                @Override
                public boolean isModified() {
                    return projectComponent.isModified();
                }

                @Override
                public void apply() {
                    projectComponent.apply();
                }

                @Override
                public void reset() {
                    projectComponent.reset();
                }

                @Override
                public void disposeUIResources() {
                    projectComponent.disposeUIResources();
                }
            });
        }
    }
}
