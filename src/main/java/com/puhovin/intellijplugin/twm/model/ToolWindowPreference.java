package com.puhovin.intellijplugin.twm.model;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

import java.io.Serializable;

@Tag("toolwindow")
public class ToolWindowPreference implements Serializable {

    @Attribute("id")
    private String id;

    @Attribute("preference")
    private AvailabilityPreference availabilityPreference;

    public ToolWindowPreference() {}

    public ToolWindowPreference(String id, AvailabilityPreference availabilityPreference) {
        this.id = id;
        this.availabilityPreference = availabilityPreference;
    }

    public String getId() {
        return id;
    }

    public AvailabilityPreference getAvailabilityPreference() {
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
