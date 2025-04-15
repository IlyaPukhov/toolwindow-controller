package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.puhovin.intellijplugin.twm.model.AvailabilityPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreference;
import com.puhovin.intellijplugin.twm.model.ToolWindowPreferenceStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(Service.Level.PROJECT)
@State(name = "ToolWindowManagerSettings", storages = {@Storage("ToolWindowManagerSettings.xml")})
public final class ToolWindowManagerService implements UnnamedConfigurable, PersistentStateComponent<ToolWindowPreferenceStore> {

    private ToolWindowPreferenceStore state;
    private final Project project;
    private final Object lock = new Object();
    private final Map<String, AvailabilityPreference> toolWindowDefaults = new HashMap<>();
    private final HashMap<String, AvailabilityPreference> toolWindowPreferences = new HashMap<>();
    private PreferredAvailabilitiesView configurationComponent;

    public ToolWindowManagerService(Project project) {
        this.project = project;
    }

    @Override
    public void initializeComponent() {

        if (state == null) {
            state = new ToolWindowPreferenceStore();
        }

        synchronized (lock) {

            final ToolWindowManager manager = ToolWindowManager.getInstance(project);

            for (final String id : manager.getToolWindowIds()) {
                if (toolWindowPreferences.get(id) == null) {
                    final ToolWindow toolWindow = manager.getToolWindow(id);

                    if (toolWindow != null) {
                        toolWindowPreferences.put(id, AvailabilityPreference.UNAFFECTED);
                    }
                }

                if (toolWindowDefaults.get(id) == null) {
                    final ToolWindow toolWindow = manager.getToolWindow(id);

                    if (toolWindow != null) {
                        final boolean isAvailable = toolWindow.isAvailable();

                        toolWindowDefaults.put(id, isAvailable ? AvailabilityPreference.AVAILABLE : AvailabilityPreference.UNAVAILABLE);
                    }
                }
            }

            Map<String, ToolWindowPreference> res = getState().getAllPreferences();
            for (ToolWindowPreference val : res.values()) {
                toolWindowPreferences.put(val.id(), val.availabilityPreference());
            }
        }
    }

    @Override
    public @Nullable ToolWindowPreferenceStore getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull ToolWindowPreferenceStore state) {
        this.state = state;
    }

    @NotNull
    public List<ToolWindowPreference> getDefaultAvailabilities() {
        final List<ToolWindowPreference> result = new ArrayList<>();

        for (final Map.Entry<String, AvailabilityPreference> toolWindowDefault : toolWindowDefaults.entrySet()) {
            result.add(new ToolWindowPreference(toolWindowDefault.getKey(), toolWindowDefault.getValue()));
        }

        return result;
    }

    @Nullable
    public AvailabilityPreference getDefaultAvailability(@NotNull String id) {
        return toolWindowDefaults.get(id);
    }

    @NotNull
    public List<ToolWindowPreference> getPreferredAvailabilities() {
        final List<ToolWindowPreference> result = new ArrayList<>();

        synchronized (lock) {
            for (final Map.Entry<String, AvailabilityPreference> toolWindowDefault : toolWindowPreferences.entrySet()) {
                result.add(new ToolWindowPreference(toolWindowDefault.getKey(), toolWindowDefault.getValue()));
            }
        }

        return result;
    }


    private void applyPreferencesFrom(@NotNull List<ToolWindowPreference> preferencesToApply) {
        final ToolWindowPreferenceApplier toolWindowPreferenceApplier = new ToolWindowPreferenceApplier(project);

        toolWindowPreferenceApplier.applyPreferencesFrom(preferencesToApply);
    }

    public JComponent createComponent() {

        configurationComponent = new PreferredAvailabilitiesView(project);

        return configurationComponent;
    }

    public boolean isModified() {
        final List<ToolWindowPreference> editedToolWindowPreferences = configurationComponent.getCurrentViewState();

        for (final ToolWindowPreference editedToolWindowPreference : editedToolWindowPreferences) {
            if (toolWindowPreferences.get(editedToolWindowPreference.id()) !=
                editedToolWindowPreference.availabilityPreference()) {
                return true;
            }
        }

        return false;
    }

    public void apply() {
        final List<ToolWindowPreference> editedToolWindowPreferences = configurationComponent.getCurrentViewState();

        synchronized (lock) {
            toolWindowPreferences.clear();

            for (final ToolWindowPreference toolWindowPreference : editedToolWindowPreferences) {
                if (toolWindowPreference != null) {
                    toolWindowPreferences.put(toolWindowPreference.id(), toolWindowPreference.availabilityPreference());
                }
            }

            ToolWindowPreferenceStore store = getState();
            Map<String, ToolWindowPreference> map = new HashMap<>();
            for (Map.Entry<String, AvailabilityPreference> entry : toolWindowPreferences.entrySet()) {
                map.put(entry.getKey(), new ToolWindowPreference(entry.getKey(), entry.getValue()));
            }
            assert store != null;
            store.setAllPreferences(map);
            loadState(store);

            applyPreferencesFrom(editedToolWindowPreferences);
        }
    }

    @Override
    public void reset() {
        if (configurationComponent != null) {
            configurationComponent.reset();
        }
    }
}
