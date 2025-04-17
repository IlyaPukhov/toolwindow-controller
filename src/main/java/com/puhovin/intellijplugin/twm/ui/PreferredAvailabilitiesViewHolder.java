package com.puhovin.intellijplugin.twm.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.puhovin.intellijplugin.twm.ToolWindowManagerDispatcher;
import org.jetbrains.annotations.NotNull;

public final class PreferredAvailabilitiesViewHolder {
    private static final Key<PreferredAvailabilitiesView> KEY = Key.create("PreferredAvailabilitiesView");

    private PreferredAvailabilitiesViewHolder() {}

    public static @NotNull PreferredAvailabilitiesView getInstance(@NotNull Project project) {
        PreferredAvailabilitiesView view = project.getUserData(KEY);
        if (view == null) {
            view = new PreferredAvailabilitiesView(project, ToolWindowManagerDispatcher.getInstance(project));
            project.putUserData(KEY, view);
        }
        return view;
    }

    public static void dispose(@NotNull Project project) {
        project.putUserData(KEY, null);
    }
}
