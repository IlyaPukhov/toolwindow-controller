package com.puhovin.intellijplugin.twm.model;

import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public enum AvailabilityPreference implements Serializable {
	AVAILABLE(ToolWindowManagerBundle.message("preference.available")),
	UNAFFECTED(ToolWindowManagerBundle.message("preference.unaffected")),
	UNAVAILABLE(ToolWindowManagerBundle.message("preference.unavailable"));

	private final String text;

	AvailabilityPreference(@NotNull String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
