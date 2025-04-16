package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
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
    private final transient Project project;
    private final AvailabilityPreferenceJTable table;

    public PreferredAvailabilitiesView(Project project) {
        super(new BorderLayout());
        this.project = project;

        ToolWindowManagerService service = project.getService(ToolWindowManagerService.class);

        JPanel topPanel = initializePanel(service);
        add(topPanel, BorderLayout.NORTH);

        table = new AvailabilityPreferenceJTable(project, new AvailabilityPreferenceTableModel());
        add(new JBScrollPane(table), BorderLayout.CENTER);

        populateTableModel();
    }

    private @NotNull JPanel initializePanel(ToolWindowManagerService service) {
        JCheckBox globalModeCheckbox = new JCheckBox("Use global settings");
        globalModeCheckbox.setSelected(service.isUsingGlobalSettings());
        globalModeCheckbox.setFocusPainted(false);
        globalModeCheckbox.addActionListener(e -> {
            boolean useGlobal = globalModeCheckbox.isSelected();
            service.setUseGlobalSettings(useGlobal);
            populateTableModel();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(globalModeCheckbox);
        return topPanel;
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
