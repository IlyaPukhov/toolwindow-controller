package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.puhovin.intellijplugin.twm.model.SettingsMode;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceJTable;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class PreferredAvailabilitiesView extends JPanel {
    private final AvailabilityPreferenceJTable table;
    private final ToolWindowManagerDispatcher dispatcher;

    public PreferredAvailabilitiesView(@NotNull Project project, @NotNull ToolWindowManagerDispatcher dispatcher) {
        super(new BorderLayout());
        this.dispatcher = dispatcher;

        JPanel topPanel = initializePanel();
        add(topPanel, BorderLayout.NORTH);

        table = new AvailabilityPreferenceJTable(project, new AvailabilityPreferenceTableModel());
        add(new JBScrollPane(table), BorderLayout.CENTER);

        populateTableModel(dispatcher.getAvailableToolWindows());
    }

    private @NotNull JPanel initializePanel() {
        JCheckBox globalModeCheckbox = new JCheckBox("Use global settings");
        globalModeCheckbox.setSelected(dispatcher.getSettingsMode().getValue());
        globalModeCheckbox.setFocusPainted(false);
        globalModeCheckbox.addActionListener(e -> {
            boolean useGlobal = globalModeCheckbox.isSelected();
            dispatcher.switchSettingsMode(SettingsMode.fromBoolean(useGlobal));
            populateTableModel(dispatcher.getAvailableToolWindows());
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(globalModeCheckbox);
        return topPanel;
    }

    private void populateTableModel(List<ToolWindowPreference> preferences) {
        AvailabilityPreferenceTableModel model = (AvailabilityPreferenceTableModel) table.getModel();
        model.removeToolWindowPreferences();
        preferences.forEach(model::addToolWindowPreference);
    }

    @NotNull
    public List<ToolWindowPreference> getCurrentViewState() {
        return ((AvailabilityPreferenceTableModel) table.getModel()).getToolWindowPreferences();
    }

    public void reset(@NotNull List<ToolWindowPreference> defaultPreferences) {
        populateTableModel(defaultPreferences);
    }
}