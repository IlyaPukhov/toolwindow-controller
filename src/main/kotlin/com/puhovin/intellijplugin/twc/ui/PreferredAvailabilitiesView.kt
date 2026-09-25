package com.puhovin.intellijplugin.twc.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import com.puhovin.intellijplugin.twc.model.SettingsMode
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.ui.table.AvailabilityPreferenceJTable
import com.puhovin.intellijplugin.twc.ui.table.AvailabilityPreferenceTableModel
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

/** Таблица предпочтений и выбор глобального или проектного режима без побочных действий до Apply. */
class PreferredAvailabilitiesView(project: Project, private val manager: ToolWindowPreferencesManager) : JPanel(BorderLayout()) {

    private val model = AvailabilityPreferenceTableModel()
    private val table = AvailabilityPreferenceJTable(project, model)
    private val globalModeCheckbox = JCheckBox("Use global settings")

    val settingsMode: SettingsMode get() = SettingsMode.fromBoolean(globalModeCheckbox.isSelected)

    init {
        add(JPanel(FlowLayout(FlowLayout.LEFT)).apply { add(globalModeCheckbox) }, BorderLayout.NORTH)
        add(JBScrollPane(table), BorderLayout.CENTER)
        globalModeCheckbox.addActionListener {
            table.cellEditor?.cancelCellEditing()
            model.setToolWindowPreferences(manager.getAvailableToolWindows(settingsMode))
        }
        reset()
    }

    /** Возвращает черновик; перед сохранением завершает текущее редактирование ячейки. */
    fun getCurrentViewState(commitEditor: Boolean = false): List<ToolWindowPreference> {
        if (commitEditor) table.cellEditor?.stopCellEditing()
        return model.getToolWindowPreferences()
    }

    /** Отбрасывает черновик и заново показывает сохранённые настройки. */
    fun reset() {
        table.cellEditor?.cancelCellEditing()
        globalModeCheckbox.isSelected = manager.settingsMode.value
        model.setToolWindowPreferences(manager.getAvailableToolWindows())
    }
}
