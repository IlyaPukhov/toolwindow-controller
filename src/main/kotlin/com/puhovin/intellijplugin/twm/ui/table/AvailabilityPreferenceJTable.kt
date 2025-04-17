package com.puhovin.intellijplugin.twm.ui.table

import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference
import org.jetbrains.annotations.NotNull
import javax.swing.ComboBoxModel
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListSelectionModel
import javax.swing.JComboBox
import javax.swing.ListSelectionModel
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableModel

class AvailabilityPreferenceJTable(
    @NotNull private val project: Project, model: TableModel
) : JBTable(model) {
    companion object {
        const val TOOL_WINDOW_ID_COLUMN_INDEX = 0
        const val AVAILABILITY_PREFERENCE_COLUMN_INDEX = 1
    }

    private val availabilityPreferenceCellEditor: TableCellEditor

    init {
        val boxModel: ComboBoxModel<AvailabilityPreference> = DefaultComboBoxModel(AvailabilityPreference.entries.toTypedArray())
        availabilityPreferenceCellEditor = DefaultCellEditor(JComboBox(boxModel))

        setAutoCreateRowSorter(true)
        rowHeight = (UIUtil.getLabelFont().size * 1.8).toInt()
        rowSorter.toggleSortOrder(0)
        createDefaultColumnsFromModel()
    }

    override fun createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel()
        columnModel.getColumn(TOOL_WINDOW_ID_COLUMN_INDEX).preferredWidth = 400
        columnModel.getColumn(AVAILABILITY_PREFERENCE_COLUMN_INDEX).preferredWidth = 200
    }

    override fun createDefaultDataModel(): TableModel {
        return AvailabilityPreferenceTableModel()
    }

    override fun createDefaultSelectionModel(): ListSelectionModel {
        val listSelectionModel = DefaultListSelectionModel()
        listSelectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        return listSelectionModel
    }

    override fun createDefaultTableHeader(): JTableHeader {
        val defaultTableHeader = super.createDefaultTableHeader()
        defaultTableHeader.reorderingAllowed = false
        defaultTableHeader.background = UIUtil.getPanelBackground()
        return defaultTableHeader
    }

    override fun getCellEditor(row: Int, column: Int): TableCellEditor {
        return if (column == AVAILABILITY_PREFERENCE_COLUMN_INDEX) {
            availabilityPreferenceCellEditor
        } else {
            super.getCellEditor(row, column)
        }
    }

    override fun getCellRenderer(row: Int, column: Int): TableCellRenderer {
        val availabilityPreference =
            getValueAt(row, AVAILABILITY_PREFERENCE_COLUMN_INDEX) as AvailabilityPreference?
        val effectivePreference = availabilityPreference ?: AvailabilityPreference.UNAFFECTED

        return when (effectivePreference) {
            AvailabilityPreference.AVAILABLE ->
                ToolWindowPreferenceCellRenderer(project, JBColor.namedColor("FileColor.Green", JBColor(0xeffae7, 0x49544a)))
            AvailabilityPreference.UNAFFECTED ->
                ToolWindowPreferenceCellRenderer(project, JBColor.WHITE)
            AvailabilityPreference.UNAVAILABLE ->
                ToolWindowPreferenceCellRenderer(project, JBColor.namedColor("FileColor.Rose", JBColor(0xf2dcda, 0x6e535b)))
        }
    }
}