package com.puhovin.intellijplugin.twm.table;

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

public class ToolWindowPreferenceCellRenderer extends JLabel implements TableCellRenderer {
    public static final Color SELECTION_BACKGROUND = UIManager.getColor("Table.selectionBackground");
    public static final Color SELECTION_FOREGROUND = UIManager.getColor("Table.selectionForeground");
    public static final Border FOCUS_CELL_HIGHLIGHT_BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");
    private final Color unselectedBackground;
    private final Color unselectedForeground;
    private final Project project;

    public ToolWindowPreferenceCellRenderer(@NotNull Project project, @NotNull Color bg) {
        unselectedBackground = bg;
        unselectedForeground = JBColor.BLACK;
        setBackground(unselectedBackground);
        setForeground(unselectedForeground);
        setOpaque(true);
        setBorder(JBUI.Borders.empty(1, 8));
        this.project = project;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setBackground(isSelected ? SELECTION_BACKGROUND : unselectedBackground);
        setBorder(hasFocus ? FOCUS_CELL_HIGHLIGHT_BORDER : null);
        setForeground(isSelected ? SELECTION_FOREGROUND : unselectedForeground);
        setText(value == null ? "" : value.toString());
        if (column == 0) {
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow((String) value);
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
