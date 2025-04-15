package com.puhovin.intellijplugin.twm.model;

import java.io.Serializable;

public class ToolWindowPreference implements Serializable {

    private String id;
    private AvailabilityPreference availabilityPreference;

    public ToolWindowPreference() {}

    public ToolWindowPreference(String id, AvailabilityPreference availabilityPreference) {
        this.id = id;
        this.availabilityPreference = availabilityPreference;
    }

    public String id() {
        return id;
    }

    public AvailabilityPreference availabilityPreference() {
        return availabilityPreference;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ToolWindowPreference that = (ToolWindowPreference) o;
        return id.equals(that.id) && availabilityPreference == that.availabilityPreference;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + availabilityPreference.hashCode();
        return result;
    }
}
