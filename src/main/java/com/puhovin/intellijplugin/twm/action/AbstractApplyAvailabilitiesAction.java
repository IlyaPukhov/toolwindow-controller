package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplyAvailabilitiesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ToolWindowManagerDispatcher dispatcher = ToolWindowManagerDispatcher.getInstance(project);

        List<ToolWindowPreference> prefs = getPreferencesToApply(dispatcher);
        Map<String, ToolWindowPreference> newPrefs = new HashMap<>();
        prefs.forEach(pref -> newPrefs.put(pref.getId(), pref));

        dispatcher.applyPreferences(newPrefs);
    }

    @NotNull
    protected abstract List<ToolWindowPreference> getPreferencesToApply(@NotNull ToolWindowManagerDispatcher dispatcher);
}
