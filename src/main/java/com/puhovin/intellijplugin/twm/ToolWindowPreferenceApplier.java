package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ToolWindowPreferenceApplier {
    private final Project project;
    private final ToolWindowManagerDispatcher dispatcher;

    public ToolWindowPreferenceApplier(@NotNull Project project) {
        this.project = project;
        this.dispatcher = ToolWindowManagerDispatcher.getInstance(project);
    }

    public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> preferences) {
        if (project.isDefault()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);

            preferences.stream()
                    .map(this::resolvePreference)
                    .forEach(pref -> applyPreference(manager, pref));
        });
    }

    private ToolWindowPreference resolvePreference(ToolWindowPreference pref) {
        if (pref.getAvailabilityPreference() != AvailabilityPreference.UNAFFECTED) {
            return pref;
        }

        return Optional.ofNullable(dispatcher.getDefaultAvailability(pref.getId()))
                .orElse(pref);
    }

    private void applyPreference(ToolWindowManager manager, ToolWindowPreference pref) {
        ToolWindow tw = manager.getToolWindow(pref.getId());
        if (tw == null) return;

        Boolean desiredState = switch (pref.getAvailabilityPreference()) {
            case AVAILABLE -> true;
            case UNAVAILABLE -> false;
            default -> null;
        };

        if (desiredState != null && tw.isAvailable() != desiredState) {
            tw.setAvailable(desiredState, null);
        }
    }
}