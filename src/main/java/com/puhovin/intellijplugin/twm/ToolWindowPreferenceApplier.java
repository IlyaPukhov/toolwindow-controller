package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolWindowPreferenceApplier {
    private final Project project;

    public ToolWindowPreferenceApplier(@NotNull Project project) {
        this.project = project;
    }

    public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> toolWindowPreferences) {
        if (!project.isDefault()) {
            final ToolWindowManagerService projectComponent = project.getService(ToolWindowManagerService.class);
            final ToolWindowManager manager = ToolWindowManager.getInstance(project);

            for (final ToolWindowPreference toolWindowPreference : toolWindowPreferences) {
                final String id = toolWindowPreference.id();
                final ToolWindow tw = manager.getToolWindow(id);

                if (tw != null) {
                    AvailabilityPreference preference = toolWindowPreference.availabilityPreference();

                    switch (preference) {
                        case AVAILABLE:
                            tw.setAvailable(true, null);
                            break;
                        case UNAFFECTED:
                            final ToolWindowPreference defaultAvailability = projectComponent.getDefaultAvailability(id);

                            if (defaultAvailability != null) {
                                tw.setAvailable(defaultAvailability.availabilityPreference() == AvailabilityPreference.AVAILABLE, null);
                            }
                            break;
                        case UNAVAILABLE:
                            tw.setAvailable(false, null);
                            break;
                    }
                }
            }
        }
    }
}
