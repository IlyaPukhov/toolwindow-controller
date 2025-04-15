package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceJTable;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PreferredAvailabilitiesView extends JPanel {
    private final Project project;
    private final AvailabilityPreferenceJTable table;
    private final JCheckBox globalModeCheckbox;

    public PreferredAvailabilitiesView(Project project) {
        super(new BorderLayout());
        this.project = project;

        ToolWindowManagerService service = project.getService(ToolWindowManagerService.class);

        globalModeCheckbox = new JCheckBox("Use global settings");
        globalModeCheckbox.setSelected(service.isUsingGlobalSettings());
        globalModeCheckbox.addActionListener(e -> {
            boolean useGlobal = globalModeCheckbox.isSelected();
            service.setUseGlobalSettings(useGlobal);
            populateTableModel();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(globalModeCheckbox);
        add(topPanel, BorderLayout.NORTH);

        table = new AvailabilityPreferenceJTable(project, new AvailabilityPreferenceTableModel());
        add(new JBScrollPane(table), BorderLayout.CENTER);

        if (!project.isDefault()) {
            populateTableModel();
        }
    }

    private void populateTableModel() {
        AvailabilityPreferenceTableModel model = (AvailabilityPreferenceTableModel) table.getModel();
        model.removeToolWindowPreferences();

        project.getService(ToolWindowManagerService.class)
                .getAvailableToolWindows()
                .forEach(model::addToolWindowPreference);
    }

    @NotNull
    public List<ToolWindowPreference> getCurrentViewState() {
        return ((AvailabilityPreferenceTableModel) table.getModel()).getToolWindowPreferences();
    }

    public void reset() {
        populateTableModel();
    }
}
