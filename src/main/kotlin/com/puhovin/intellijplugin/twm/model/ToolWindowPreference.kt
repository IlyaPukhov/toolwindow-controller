package com.puhovin.intellijplugin.twm.model

import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.io.Serializable

@Tag("toolwindow")
data class ToolWindowPreference(
    @Attribute("id")
    val id: String,

    @Attribute(value = "preference", converter = AvailabilityPreferenceConverter::class)
    val availabilityPreference: AvailabilityPreference
) : Serializable {

    class AvailabilityPreferenceConverter : Converter<AvailabilityPreference>() {

        override fun fromString(value: String): AvailabilityPreference =
            try {
                AvailabilityPreference.valueOf(value)
            } catch (_: IllegalArgumentException) {
                AvailabilityPreference.UNAFFECTED
            }

        override fun toString(value: AvailabilityPreference): String = value.name
    }
}