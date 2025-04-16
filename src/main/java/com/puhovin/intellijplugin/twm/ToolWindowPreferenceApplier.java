package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
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
        if (project.isDefault()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
                    final ToolWindowManagerService projectComponent = project.getService(ToolWindowManagerService.class);
                    final ToolWindowManager manager = ToolWindowManager.getInstance(project);

                    for (final ToolWindowPreference toolWindowPreference : toolWindowPreferences) {
                        final String id = toolWindowPreference.id();
                        final ToolWindow tw = manager.getToolWindow(id);

                        if (tw != null) {
                            AvailabilityPreference preference = toolWindowPreference.availabilityPreference();

                            boolean newAvailableState = false;

                            switch (preference) {
                                case AVAILABLE:
                                    newAvailableState = true;
                                    break;
                                case UNAFFECTED:
                                    final ToolWindowPreference defaultAvailability = projectComponent.getDefaultAvailability(id);
                                    if (defaultAvailability != null) {
                                        newAvailableState = (defaultAvailability.availabilityPreference() == AvailabilityPreference.AVAILABLE);
                                    }
                                    break;
                                case UNAVAILABLE:
                                    break;
                            }

                            if (tw.isAvailable() != newAvailableState) {
                                tw.setAvailable(newAvailableState, null);
                            }
                        }
                    }
                }
        );
    }

}
