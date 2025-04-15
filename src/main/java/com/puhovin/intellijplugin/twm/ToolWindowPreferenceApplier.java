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
package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Mark Scott
 * @version $Id: ToolWindowPreferenceApplier.java 31 2007-06-01 23:01:54Z mark $
 */
public class ToolWindowPreferenceApplier {
	private final Project project;

	public ToolWindowPreferenceApplier(@NotNull Project project) {
		this.project = project;
	}

	public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> toolWindowPreferences) {
		if (!project.isDefault()) {
			final ToolWindowManagerService projectComponent = project.getService(ToolWindowManagerService.class);
			final ToolWindowManager manager = ToolWindowManager.getInstance(project);

			for (final ToolWindowPreference toolWindowPreference : toolWindowPreferences) {
				final String id = toolWindowPreference.getId();
				final ToolWindow tw = manager.getToolWindow(id);

				if (tw != null) {
					final AvailabilityPreference preference = toolWindowPreference.getAvailabilityPreference();

					switch (preference) {
						case AVAILABLE:
							tw.setAvailable(true, null);
							break;
						case UNAFFECTED:
							final AvailabilityPreference defaultAvailability = projectComponent.getDefaultAvailability(id);

							if (defaultAvailability != null) {
								tw.setAvailable(defaultAvailability == AvailabilityPreference.AVAILABLE, null);
							}
							break;
						case UNAVAILABLE:
							tw.setAvailable(false, null);
							break;
						default:
							// Shouldn't be reached...
					}
				}
			}
		}
	}
}
