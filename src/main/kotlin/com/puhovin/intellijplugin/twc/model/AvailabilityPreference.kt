package com.puhovin.intellijplugin.twc.model

import com.puhovin.intellijplugin.twc.util.ToolWindowControllerBundle
import java.io.Serializable

enum class AvailabilityPreference(val text: String) : Serializable {
    AVAILABLE(ToolWindowControllerBundle.message("preference.available")),
    UNAFFECTED(ToolWindowControllerBundle.message("preference.unaffected")),
    UNAVAILABLE(ToolWindowControllerBundle.message("preference.unavailable"));

    override fun toString() = text
}