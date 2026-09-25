package com.puhovin.intellijplugin.twc.model

import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import java.io.Serializable

/** Сериализуемый список переопределений доступности окон. Некорректные записи не участвуют в работе. */
@Tag("component")
data class ToolWindowPreferenceStore(
    @XCollection
    val preferences: MutableList<ToolWindowPreference> = mutableListOf()
) : Serializable {

    /** Заменяет сохранённые переопределения, исключая пустые ID и значение по умолчанию. */
    fun setPreferences(preferencesMap: Map<String, ToolWindowPreference?>) {
        val updated = preferencesMap.mapNotNull { (id, preference) ->
            val availability = preference?.availabilityPreference
            if (id.isBlank() || availability == null || availability == AvailabilityPreference.UNAFFECTED) null
            else ToolWindowPreference(id, availability)
        }
        preferences.clear()
        preferences.addAll(updated)
    }

    /** Возвращает отдельную карту корректных записей, не отдавая изменяемые объекты состояния. */
    fun getPreferences(): Map<String, ToolWindowPreference> = buildMap {
        for (preference in preferences) {
            val id = preference.id?.takeIf { it.isNotBlank() } ?: continue
            val availability = preference.availabilityPreference ?: continue
            if (availability != AvailabilityPreference.UNAFFECTED) put(id, ToolWindowPreference(id, availability))
        }
    }
}
