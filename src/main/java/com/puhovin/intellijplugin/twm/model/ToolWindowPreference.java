package com.puhovin.intellijplugin.twm.model;

import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Tag("toolwindow")
public class ToolWindowPreference implements Serializable {

    @Attribute("id")
    private String id;

    @Attribute(value = "preference", converter = AvailabilityPreferenceConverter.class)
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

    public static class AvailabilityPreferenceConverter extends Converter<AvailabilityPreference> {

        @Override
        public AvailabilityPreference fromString(@NotNull String value) {
            return AvailabilityPreference.valueOf(value);
        }

        @Override
        public String toString(AvailabilityPreference value) {
            return value.name();
        }
    }
}
