package com.puhovin.intellijplugin.twm.model;

import java.io.Serializable;

public record ToolWindowPreference(String id,
                                   AvailabilityPreference availabilityPreference) implements Serializable {
}
