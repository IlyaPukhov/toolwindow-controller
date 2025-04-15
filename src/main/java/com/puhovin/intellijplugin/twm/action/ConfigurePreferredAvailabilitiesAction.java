/*
 * Copyright 2007 Mark Scott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Mark Scott
 * @version $Id: ConfigurePreferredAvailabilitiesAction.java 31 2007-06-01 23:01:54Z mark $
 */
public class ConfigurePreferredAvailabilitiesAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		final Project project = (Project) e.getDataContext().getData(CommonDataKeys.PROJECT.getName());

		if (project != null && !project.isDefault()) {
			final ToolWindowManagerService projectComponent =
					project.getService(ToolWindowManagerService.class);

			ShowSettingsUtil.getInstance().editConfigurable(project, new Configurable() {
				@Nls
				public String getDisplayName() {
					return ToolWindowManagerBundle.message("configurable.display.name");
				}

				public Icon getIcon() {
					return null;
				}

				@Nullable
				@NonNls
				public String getHelpTopic() {
					return null;
				}

				public JComponent createComponent() {
					return projectComponent.createComponent();
				}

				public boolean isModified() {
					return projectComponent.isModified();
				}

				public void apply() throws ConfigurationException {
					projectComponent.apply();
				}

				public void reset() {
					projectComponent.reset();
				}

				public void disposeUIResources() {
					projectComponent.disposeUIResources();
				}
			});
		}
	}
}
