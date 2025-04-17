package com.puhovin.intellijplugin.twm.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher
import com.puhovin.intellijplugin.twm.model.SettingsMode
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference
import com.puhovin.intellijplugin.twm.ui.table.AvailabilityPreferenceJTable
import com.puhovin.intellijplugin.twm.ui.table.AvailabilityPreferenceTableModel
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JPanel
import org.jetbrains.annotations.NotNull

class PreferredAvailabilitiesView(
    @NotNull project: Project,
    @NotNull private val dispatcher: ToolWindowManagerDispatcher
) : JPanel(BorderLayout()) {

    private val table: AvailabilityPreferenceJTable

    init {
        val topPanel = initializePanel()
        add(topPanel, BorderLayout.NORTH)

        table = AvailabilityPreferenceJTable(project, AvailabilityPreferenceTableModel())
        add(JBScrollPane(table), BorderLayout.CENTER)

        populateTableModel(dispatcher.getAvailableToolWindows())
    }

    private fun initializePanel(): JPanel {
        val globalModeCheckbox = JCheckBox("Use global settings")
        globalModeCheckbox.isSelected = dispatcher.getSettingsMode().value
        globalModeCheckbox.addActionListener {
            val useGlobal = globalModeCheckbox.isSelected
            dispatcher.switchSettingsMode(SettingsMode.fromBoolean(useGlobal))
            populateTableModel(dispatcher.getAvailableToolWindows())
        }

        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(globalModeCheckbox)
        }
    }

    private fun populateTableModel(preferences: List<ToolWindowPreference>) {
        val model = table.model as AvailabilityPreferenceTableModel
        model.setToolWindowPreferences(preferences)
    }

    @NotNull
    fun getCurrentViewState(): List<ToolWindowPreference> {
        return (table.model as AvailabilityPreferenceTableModel).getToolWindowPreferences()
    }

    fun reset(@NotNull defaultPreferences: List<ToolWindowPreference>) {
        populateTableModel(defaultPreferences)
    }
}
