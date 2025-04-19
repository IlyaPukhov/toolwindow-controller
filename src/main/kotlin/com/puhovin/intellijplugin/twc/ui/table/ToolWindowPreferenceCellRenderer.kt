package com.puhovin.intellijplugin.twc.ui.table

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.EmptyIcon
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.border.Border
import javax.swing.table.TableCellRenderer

class ToolWindowPreferenceCellRenderer(
    private val project: Project,
    bg: Color
) : JLabel(), TableCellRenderer {

    companion object {
        val SELECTION_BACKGROUND: Color = UIManager.getColor("Table.selectionBackground")
        val SELECTION_FOREGROUND: Color = UIManager.getColor("Table.selectionForeground")
        val FOCUS_CELL_HIGHLIGHT_BORDER: Border = UIManager.getBorder("Table.focusCellHighlightBorder")
    }

    private val unselectedBackground: Color = bg
    private val unselectedForeground: Color = JBColor.BLACK

    init {
        background = unselectedBackground
        foreground = unselectedForeground
        isOpaque = true
        border = JBUI.Borders.empty(1, 8)
    }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        background = if (isSelected) SELECTION_BACKGROUND else unselectedBackground
        border = if (hasFocus) FOCUS_CELL_HIGHLIGHT_BORDER else null
        foreground = if (isSelected) SELECTION_FOREGROUND else unselectedForeground
        text = value?.toString() ?: ""

        if (column == 0) {
            val window: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow(value as String)
            if (window != null && window.icon != null) {
                icon = window.icon
                iconTextGap = 10
            } else {
                icon = EmptyIcon.ICON_13
                iconTextGap = 10
            }
        }

        border = JBUI.Borders.empty(1, 8)

        return this
    }
}