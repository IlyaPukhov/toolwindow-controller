package com.puhovin.intellijplugin.twm.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public record ToolWindowPreference(String id,
                                   AvailabilityPreference availabilityPreference
) implements Comparable<ToolWindowPreference>, Serializable {

    public int compareTo(@NotNull ToolWindowPreference o) {
        return id.compareTo(o.id);
    }
}
