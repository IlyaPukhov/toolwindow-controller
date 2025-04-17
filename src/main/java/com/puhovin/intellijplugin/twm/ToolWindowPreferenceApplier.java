package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.AVAILABLE;
import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAFFECTED;

public class ToolWindowPreferenceApplier {
    private final Project project;
    private final ToolWindowManagerDispatcher dispatcher;

    public ToolWindowPreferenceApplier(@NotNull Project project, @NotNull ToolWindowManagerDispatcher dispatcher) {
        this.project = project;
        this.dispatcher = dispatcher;
    }

    public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> preferences) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);

            for (ToolWindowPreference pref : preferences) {
                ToolWindowPreference resolved = resolvePreference(pref);
                applyPreference(manager, resolved);
            }
        });
    }

    private ToolWindowPreference resolvePreference(@NotNull ToolWindowPreference pref) {
        if (pref.getAvailabilityPreference() == UNAFFECTED) {
            ToolWindowPreference defaultPref = dispatcher.getDefaultAvailabilityToolWindow(pref.getId());
            if (defaultPref != null) {
                return new ToolWindowPreference(pref.getId(), defaultPref.getAvailabilityPreference());
            }
        }
        return pref;
    }

    private void applyPreference(@NotNull ToolWindowManager manager, @NotNull ToolWindowPreference pref) {
        ToolWindow tw = manager.getToolWindow(pref.getId());
        if (tw == null) return;

        AvailabilityPreference preference = pref.getAvailabilityPreference();
        if (preference == UNAFFECTED) return;

        tw.setAvailable((preference == AVAILABLE), null);
    }
}