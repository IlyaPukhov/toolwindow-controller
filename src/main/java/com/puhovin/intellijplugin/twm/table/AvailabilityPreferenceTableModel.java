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

import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mark Scott
 * @version $Id: AvailabilityPreferenceTableModel.java 31 2007-06-01 23:01:54Z mark $
 */
public class AvailabilityPreferenceTableModel extends AbstractTableModel {
	private static final String[] HEADERS = new String[]{
			ToolWindowManagerBundle.message("availability.preference.table.model.column.0"),
			ToolWindowManagerBundle.message("availability.preference.table.model.column.1")};

	@NonNls
	private static final String INVALID_ROW_INDEX_MESSAGE =
			"Invalid row index (should be in range {0} to {1}): {2}";
	@NonNls
	private static final String INVALID_COLUMN_INDEX_MESSAGE = "Invalid column index (should be 0 or 1): {0}";

	private final List<ToolWindowPreference> toolWindowPreferences;

	public AvailabilityPreferenceTableModel() {
		super();
		toolWindowPreferences = new ArrayList<ToolWindowPreference>();
	}

	private String getToolWindowId(final int rowIndex) {
		if (rowIndex < 0 || rowIndex >= toolWindowPreferences.size()) {
			throw new IllegalArgumentException(
					MessageFormat.format(INVALID_ROW_INDEX_MESSAGE, 0, toolWindowPreferences.size() - 1, rowIndex));
		}

		final String result = toolWindowPreferences.get(rowIndex).getId();
		return result;
	}

	private AvailabilityPreference getAvailabilityPreference(final int rowIndex) {
		if (rowIndex < 0 || rowIndex >= toolWindowPreferences.size()) {
			throw new IllegalArgumentException(
					MessageFormat.format(INVALID_ROW_INDEX_MESSAGE, 0, toolWindowPreferences.size() - 1, rowIndex));
		}

		final AvailabilityPreference result = toolWindowPreferences.get(rowIndex).getAvailabilityPreference();
		return result;
	}

	public void addToolWindowPreference(@NotNull final ToolWindowPreference toolWindowPreference) {
		toolWindowPreferences.add(toolWindowPreference);
		fireTableDataChanged();
	}

	public @NotNull List<ToolWindowPreference> getToolWindowPreferences() {
		return Collections.unmodifiableList(toolWindowPreferences);
	}

	public void removeToolWindowPreferences() {
		toolWindowPreferences.clear();
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return HEADERS.length;
	}

	@Override
	public String getColumnName(final int column) {
		if (column < 0 || column >= HEADERS.length) {
			throw new IllegalArgumentException(
					"Column number should be in range 0 to " + (HEADERS.length - 1) + ": " + column);
		}

		return HEADERS[column];
	}

	public int getRowCount() {
		return toolWindowPreferences.size();
	}

	public Object getValueAt(final int rowIndex, final int columnIndex) {
		return switch (columnIndex) {
			case 0 -> getToolWindowId(rowIndex);
			case 1 -> getAvailabilityPreference(rowIndex);
			default ->
					throw new IllegalArgumentException(MessageFormat.format(INVALID_COLUMN_INDEX_MESSAGE, columnIndex));
		};
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == AvailabilityPreferenceJTable.AVAILABILITY_PREFERENCE_COLUMN_INDEX;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		final ToolWindowPreference currentRowValue = toolWindowPreferences.get(rowIndex);

		final ToolWindowPreference newRowValue = switch (columnIndex) {
			case AvailabilityPreferenceJTable.TOOL_WINDOW_ID_COLUMN_INDEX ->
					new ToolWindowPreference((String) aValue, currentRowValue.getAvailabilityPreference());
			case AvailabilityPreferenceJTable.AVAILABILITY_PREFERENCE_COLUMN_INDEX ->
					new ToolWindowPreference(currentRowValue.getId(), (AvailabilityPreference) aValue);
			default -> null;
		};

		if (newRowValue != null) {
			toolWindowPreferences.set(rowIndex, newRowValue);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}
}
