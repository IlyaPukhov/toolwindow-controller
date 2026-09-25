package com.puhovin.intellijplugin.twc.ui.table

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.ui.EmptyIcon
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.table.TableCellRenderer

/** Общий рендерер столбцов: при переиспользовании очищает иконку вне столбца окна. */
class ToolWindowPreferenceCellRenderer(private val project: Project, private val unselectedBackground: Color) : JLabel(), TableCellRenderer {

    private val padding = JBUI.Borders.empty(1, 8)

    init {
        isOpaque = true
        iconTextGap = 10
    }

    override fun getTableCellRendererComponent(
        table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
    ): Component {
        background = if (isSelected) table.selectionBackground else unselectedBackground
        foreground = if (isSelected) table.selectionForeground else table.foreground
        border = if (hasFocus) UIManager.getBorder("Table.focusCellHighlightBorder") ?: padding else padding
        text = value?.toString().orEmpty()
        icon = if (column == 0) {
            ToolWindowManager.getInstance(project).getToolWindow(value as? String)?.icon ?: EmptyIcon.ICON_13
        } else null
        return this
    }
}
