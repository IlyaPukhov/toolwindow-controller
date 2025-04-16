package com.puhovin.intellijplugin.twm.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.puhovin.intellijplugin.twm.ToolWindowManagerService;
import com.puhovin.intellijplugin.twm.ToolWindowPreferenceApplier;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplyAvailabilitiesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null || project.isDefault()) return;

        ToolWindowManagerService service = project.getService(ToolWindowManagerService.class);
        List<ToolWindowPreference> prefs = getPreferencesToApply(service);

        Map<String, ToolWindowPreference> newPrefs = new HashMap<>();
        prefs.forEach(p -> newPrefs.put(p.getId(), p));

        assert service.getState() != null;
        service.getState().setAllPreferences(newPrefs);

        new ToolWindowPreferenceApplier(project).applyPreferencesFrom(prefs);
    }

    @NotNull
    protected abstract List<ToolWindowPreference> getPreferencesToApply(@NotNull ToolWindowManagerService service);
}
