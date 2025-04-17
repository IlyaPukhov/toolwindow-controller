package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import com.puhovin.intellijplugin.twm.ToolWindowManagerDispatcher;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class ConfigurePreferredAvailabilitiesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ToolWindowManagerDispatcher dispatcher = ToolWindowManagerDispatcher.getInstance(project);
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
                return dispatcher.getConfigurationComponent();
            }

            @Override
            public boolean isModified() {
                return dispatcher.isModified();
            }

            @Override
            public void apply() {
                dispatcher.apply();
            }

            @Override
            public void reset() {
                dispatcher.reset();
            }

            @Override
            public void disposeUIResources() {
                dispatcher.disposeUIResources();
            }
        });
    }
}