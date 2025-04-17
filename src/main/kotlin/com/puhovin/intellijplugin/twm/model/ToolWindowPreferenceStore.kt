package com.puhovin.intellijplugin.twm.model

import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import java.io.Serializable

@Tag("component")
data class ToolWindowPreferenceStore(
    @XCollection
    val preferences: MutableList<ToolWindowPreference> = mutableListOf()
) : Serializable {

    fun setPreferences(preferencesMap: Map<String, ToolWindowPreference?>) {
        preferences.clear()
        preferencesMap.forEach { (id, pref) ->
            pref?.let {
                preferences.add(ToolWindowPreference(id, it.availabilityPreference))
            }
        }
    }

    fun getPreferences(): Map<String, ToolWindowPreference> {
        return preferences.associateBy { it.id }
    }
}
