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
package com.puhovin.intellijplugin.twm.table;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.UIUtil;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * @author Mark Scott
 * @version $Id: AvailabilityPreferenceJTable.java 31 2007-06-01 23:01:54Z mark $
 */
public class AvailabilityPreferenceJTable extends JBTable {
	public static final int TOOL_WINDOW_ID_COLUMN_INDEX = 0;
	public static final int AVAILABILITY_PREFERENCE_COLUMN_INDEX = 1;
	private final TableCellEditor availabilityPreferenceCellEditor;
	private final Project project;

	public AvailabilityPreferenceJTable(@NotNull Project project, TableModel model) {
		super(model);
		this.project = project;
		ComboBoxModel<AvailabilityPreference> boxModel = new DefaultComboBoxModel<>(AvailabilityPreference.values());
		availabilityPreferenceCellEditor = new DefaultCellEditor(new JComboBox<AvailabilityPreference>(boxModel));
		setAutoCreateRowSorter(true);
		setRowHeight((int) (UIUtil.getLabelFont().getSize()*1.8));
		getRowSorter().toggleSortOrder(0);
	}

	@Override
	public void createDefaultColumnsFromModel() {
		super.createDefaultColumnsFromModel();
		getColumn(getColumnName(TOOL_WINDOW_ID_COLUMN_INDEX)).setPreferredWidth(400);
		getColumn(getColumnName(AVAILABILITY_PREFERENCE_COLUMN_INDEX)).setPreferredWidth(200);
	}

	@Override
	protected TableModel createDefaultDataModel() {
		return new AvailabilityPreferenceTableModel();
	}

	@Override
	protected ListSelectionModel createDefaultSelectionModel() {
		final ListSelectionModel listSelectionModel = new DefaultListSelectionModel();

		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return listSelectionModel;
	}

	@Override
	protected @NotNull JTableHeader createDefaultTableHeader() {
		final JTableHeader defaultTableHeader = super.createDefaultTableHeader();

		defaultTableHeader.setReorderingAllowed(false);
		defaultTableHeader.setBackground(UIUtil.getPanelBackground());

		return defaultTableHeader;
	}

	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		final TableCellEditor result;

		if (column == AVAILABILITY_PREFERENCE_COLUMN_INDEX) {
			result = availabilityPreferenceCellEditor;
		} else {
			result = super.getCellEditor(row, column);
		}

		return result;
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		final TableCellRenderer result;
		final AvailabilityPreference availabilityPreference =
				(AvailabilityPreference) getValueAt(row, AvailabilityPreferenceJTable.AVAILABILITY_PREFERENCE_COLUMN_INDEX);

		result = switch (availabilityPreference) {
			case AVAILABLE -> new ToolWindowPreferenceCellRenderer(project, JBColor.namedColor("FileColor.Green", new JBColor(0xeffae7, 0x49544a)));
			case UNAFFECTED -> new ToolWindowPreferenceCellRenderer(project, JBColor.WHITE);
			case UNAVAILABLE -> new ToolWindowPreferenceCellRenderer(project, JBColor.namedColor("FileColor.Rose", new JBColor(0xf2dcda, 0x6e535b)));
			default -> null; // Should never be reached
		};

		return result;
	}
}
