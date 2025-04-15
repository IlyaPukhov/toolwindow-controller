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
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.ToolWindowPreferenceApplier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Mark Scott
 * @version $Id: AbstractApplyAvailabilitiesAction.java 31 2007-06-01 23:01:54Z mark $
 */
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
