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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Mark Scott
 * @version $Id: ToolWindowPreferenceCellRenderer.java 31 2007-06-01 23:01:54Z mark $
 */
public class ToolWindowPreferenceCellRenderer extends JLabel implements TableCellRenderer {
	//public static final ToolWindowPreferenceCellRenderer AVAILABLE = new ToolWindowPreferenceCellRenderer(paler(JBColor.GREEN));
	//public static final ToolWindowPreferenceCellRenderer UNAFFECTED = new ToolWindowPreferenceCellRenderer(JBColor.WHITE);
	//public static final ToolWindowPreferenceCellRenderer UNAVAILABLE = new ToolWindowPreferenceCellRenderer(paler(JBColor.RED));
	public  static final Color SELECTION_BACKGROUND = UIManager.getColor("Table.selectionBackground");
	public  static final Color SELECTION_FOREGROUND = UIManager.getColor("Table.selectionForeground");
	public  static final Border FOCUS_CELL_HIGHLIGHT_BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");
	private final Color unselectedBackground;
	private final Color unselectedForeground;

	private Project project;

	public ToolWindowPreferenceCellRenderer(@NotNull Project project, @NotNull Color bg) {
		unselectedBackground = bg;
		unselectedForeground = JBColor.BLACK;
		setBackground(unselectedBackground);
		setForeground(unselectedForeground);
		setOpaque(true);
		setBorder(JBUI.Borders.empty(1, 8));
		this.project = project;
	}

	@NotNull
	public static Color paler(@NotNull Color c) {
		final int r = ((c.getRed() << 1) + Color.WHITE.getRed() * 3) / 5;
		final int g = ((c.getGreen() << 1) + Color.WHITE.getGreen() * 3) / 5;
		final int b = ((c.getBlue() << 1) + Color.WHITE.getBlue() * 3) / 5;

		return new JBColor(new Color(r, g, b), new Color(r, g, b));
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		setBackground(isSelected ? SELECTION_BACKGROUND : unselectedBackground);
		setBorder(hasFocus ? FOCUS_CELL_HIGHLIGHT_BORDER : null);
		setForeground(isSelected ? SELECTION_FOREGROUND : unselectedForeground);
		setText(value == null ? "" : value.toString());
		if (column == 0) {
			ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow((String)value);
			if (window != null && window.getIcon() != null) {
				setIcon(window.getIcon());
				setIconTextGap(10);
			} else {
				setIcon(EmptyIcon.ICON_13);
				setIconTextGap(10);
			}

		}

		setBorder(JBUI.Borders.empty(1, 8));

		return this;
	}
}
