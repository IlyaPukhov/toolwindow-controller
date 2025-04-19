package com.puhovin.intellijplugin.twc.ui.table

import com.puhovin.intellijplugin.twc.model.AvailabilityPreference
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.util.ToolWindowControllerBundle
import java.text.MessageFormat
import javax.swing.table.AbstractTableModel

class AvailabilityPreferenceTableModel : AbstractTableModel() {
    private val toolWindowPreferences: MutableList<ToolWindowPreference> = ArrayList()

    companion object {
        private val HEADERS = arrayOf(
            ToolWindowControllerBundle.message("availability.preference.table.model.column.0"),
            ToolWindowControllerBundle.message("availability.preference.table.model.column.1")
        )

        private const val INVALID_ROW_INDEX_MESSAGE =
            "Invalid row index (should be in range {0} to {1}): {2}"
        private const val INVALID_COLUMN_INDEX_MESSAGE = "Invalid column index (should be 0 or 1): {0}"
    }

    private fun getToolWindowId(rowIndex: Int): String {
        require(rowIndex in 0 until toolWindowPreferences.size) {
            MessageFormat.format(INVALID_ROW_INDEX_MESSAGE, 0, toolWindowPreferences.size - 1, rowIndex)
        }
        return toolWindowPreferences[rowIndex].id!!
    }

    private fun getAvailabilityPreference(rowIndex: Int): AvailabilityPreference {
        require(rowIndex in 0 until toolWindowPreferences.size) {
            MessageFormat.format(INVALID_ROW_INDEX_MESSAGE, 0, toolWindowPreferences.size - 1, rowIndex)
        }
        return toolWindowPreferences[rowIndex].availabilityPreference!!
    }

    fun setToolWindowPreferences(newPreferences: List<ToolWindowPreference>) {
        toolWindowPreferences.clear()
        toolWindowPreferences.addAll(newPreferences)
        fireTableDataChanged()
    }

    fun getToolWindowPreferences(): List<ToolWindowPreference> {
        return toolWindowPreferences.toList()
    }

    override fun getColumnCount(): Int = HEADERS.size

    override fun getColumnName(column: Int): String {
        require(column in 0 until HEADERS.size) {
            "Column number should be in range 0 to ${HEADERS.size - 1}: $column"
        }
        return HEADERS[column]
    }

    override fun getRowCount(): Int = toolWindowPreferences.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return when (columnIndex) {
            0 -> getToolWindowId(rowIndex)
            1 -> getAvailabilityPreference(rowIndex)
            else -> throw IllegalArgumentException(MessageFormat.format(INVALID_COLUMN_INDEX_MESSAGE, columnIndex))
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == AvailabilityPreferenceJTable.AVAILABILITY_PREFERENCE_COLUMN_INDEX
    }

    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
        val currentRowValue = toolWindowPreferences[rowIndex]
        val newRowValue = when (columnIndex) {
            AvailabilityPreferenceJTable.TOOL_WINDOW_ID_COLUMN_INDEX -> {
                ToolWindowPreference(aValue as String, currentRowValue.availabilityPreference!!)
            }

            AvailabilityPreferenceJTable.AVAILABILITY_PREFERENCE_COLUMN_INDEX -> {
                ToolWindowPreference(currentRowValue.id!!, aValue as AvailabilityPreference)
            }

            else -> null
        }

        newRowValue?.let {
            toolWindowPreferences[rowIndex] = it
            fireTableRowsUpdated(rowIndex, rowIndex)
        }
    }
}