package com.puhovin.intellijplugin.twm.model;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Tag("component")
public class ToolWindowPreferenceStore implements Serializable {

    @XCollection
    private final List<ToolWindowPreference> preferences = new ArrayList<>();

    public void setAllPreferences(Map<String, ToolWindowPreference> preferencesMap) {
        preferences.clear();
        preferencesMap.forEach((id, pref) -> preferences.add(new ToolWindowPreference(id, pref.getAvailabilityPreference())));
    }

    public Map<String, ToolWindowPreference> getAllPreferences() {
        return preferences.stream()
                .collect(toMap(ToolWindowPreference::getId, Function.identity()));
    }
}
