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
import com.intellij.ui.components.JBScrollPane;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceJTable;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Mark Scott
 * @version $Id: PreferredAvailabilitiesView.java 31 2007-06-01 23:01:54Z mark $
 */
public class PreferredAvailabilitiesView extends JPanel {
	private final Project project;
	private final JScrollPane scrollPane;
	private final AvailabilityPreferenceJTable table;

	public PreferredAvailabilitiesView(Project project) {
		this.project = project;
		table = new AvailabilityPreferenceJTable(project, new AvailabilityPreferenceTableModel());
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		scrollPane = new JBScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		add(scrollPane, BorderLayout.CENTER);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Component component = scrollPane.getViewport().getView();

				if (component != null) {
					scrollPane.getViewport().setBackground(component.getBackground());
				}
			}
		});

		if (project != null && !project.isDefault()) {
			populateTableModel();
		}
	}

	private void populateTableModel() {
		final AvailabilityPreferenceTableModel tableModel = (AvailabilityPreferenceTableModel) table.getModel();
		final ToolWindowManagerService component = project.getService(ToolWindowManagerService.class);
		final List<ToolWindowPreference> toolWindowPreferences = component.getPreferredAvailabilities();

		tableModel.removeToolWindowPreferences();

		for (final ToolWindowPreference toolWindowPreference : toolWindowPreferences) {
			tableModel.addToolWindowPreference(toolWindowPreference);
		}
	}

	@NotNull List<ToolWindowPreference> getCurrentViewState() {
		final AvailabilityPreferenceTableModel model = (AvailabilityPreferenceTableModel) table.getModel();
		return Collections.unmodifiableList(model.getToolWindowPreferences());
	}

	public void reset() {
		populateTableModel();
	}
}
