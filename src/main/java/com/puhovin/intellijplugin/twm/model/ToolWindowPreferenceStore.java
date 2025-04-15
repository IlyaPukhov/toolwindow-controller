package com.puhovin.intellijplugin.twm.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ToolWindowPreferenceStore implements Serializable {

	Map<String, ToolWindowPreference> allPreferences = new HashMap<>();


	public ToolWindowPreferenceStore() {
	}

	public ToolWindowPreferenceStore(Map<String, ToolWindowPreference> allPreferences) {
		this.allPreferences = allPreferences;
	}

	@NotNull
	public Map<String, ToolWindowPreference> getAllPreferences() {
		return allPreferences;
	}

	public void setAllPreferences(@NotNull Map<String, ToolWindowPreference> allPreferences) {
		this.allPreferences = allPreferences;
	}

}
