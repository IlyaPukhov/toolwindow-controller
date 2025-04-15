package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceJTable;
import com.puhovin.intellijplugin.twm.table.AvailabilityPreferenceTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PreferredAvailabilitiesView extends JPanel {
    private final Project project;
    private final JScrollPane scrollPane;
    private final AvailabilityPreferenceJTable table;

    public PreferredAvailabilitiesView(Project project) {
        this.project = project;
        table = new AvailabilityPreferenceJTable(project, new AvailabilityPreferenceTableModel());
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            Component component = scrollPane.getViewport().getView();

            if (component != null) {
                scrollPane.getViewport().setBackground(component.getBackground());
            }
        });

        if (!project.isDefault()) {
            populateTableModel();
        }
    }

    private void populateTableModel() {
        final AvailabilityPreferenceTableModel tableModel = (AvailabilityPreferenceTableModel) table.getModel();
        final ToolWindowManagerService component = project.getService(ToolWindowManagerService.class);
        final List<ToolWindowPreference> toolWindowPreferences = component.getPreferredAvailabilities();

        tableModel.removeToolWindowPreferences();

        for (final ToolWindowPreference toolWindowPreference : toolWindowPreferences) {
            tableModel.addToolWindowPreference(toolWindowPreference);
        }
    }

    @NotNull List<ToolWindowPreference> getCurrentViewState() {
        final AvailabilityPreferenceTableModel model = (AvailabilityPreferenceTableModel) table.getModel();
        return Collections.unmodifiableList(model.getToolWindowPreferences());
    }

    public void reset() {
        populateTableModel();
    }
}
