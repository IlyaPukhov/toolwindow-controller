package com.puhovin.intellijplugin.twm.model

import com.puhovin.intellijplugin.twm.util.ToolWindowManagerBundle
import java.io.Serializable

enum class AvailabilityPreference(val text: String) : Serializable {
    AVAILABLE(ToolWindowManagerBundle.message("preference.available")),
    UNAFFECTED(ToolWindowManagerBundle.message("preference.unaffected")),
    UNAVAILABLE(ToolWindowManagerBundle.message("preference.unavailable"));

    override fun toString() = text
}