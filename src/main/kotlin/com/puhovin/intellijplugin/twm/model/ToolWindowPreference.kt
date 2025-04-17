package com.puhovin.intellijplugin.twm.model

import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.io.Serializable

@Tag("toolwindow")
class ToolWindowPreference() : Serializable {

    @Attribute("id")
    var id: String? = null

    @Attribute(value = "preference", converter = AvailabilityPreferenceConverter::class)
    var availabilityPreference: AvailabilityPreference? = null

    constructor(id: String, availabilityPreference: AvailabilityPreference) : this() {
        this.id = id
        this.availabilityPreference = availabilityPreference
    }

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