package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.AVAILABLE;
import static com.puhovin.intellijplugin.twm.model.AvailabilityPreference.UNAFFECTED;

public class ToolWindowPreferenceApplier {
    private static final Key<ToolWindowPreferenceApplier> KEY = Key.create("ToolWindowPreferenceApplier");
    private final Project project;

    private ToolWindowPreferenceApplier(@NotNull Project project) {
        this.project = project;
    }

    public static ToolWindowPreferenceApplier getInstance(@NotNull Project project) {
        ToolWindowPreferenceApplier instance = project.getUserData(KEY);
        if (instance == null) {
            instance = new ToolWindowPreferenceApplier(project);
            project.putUserData(KEY, instance);
        }
        return instance;
    }

    public void applyPreferencesFrom(@NotNull List<ToolWindowPreference> preferences) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindowManagerDispatcher dispatcher = ToolWindowManagerDispatcher.getInstance(project);

            for (ToolWindowPreference pref : preferences) {
                ToolWindowPreference resolved = resolvePreference(pref, dispatcher);
                applyPreference(manager, resolved);
            }
        });
    }

    private ToolWindowPreference resolvePreference(@NotNull ToolWindowPreference pref, @NotNull ToolWindowManagerDispatcher dispatcher) {
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