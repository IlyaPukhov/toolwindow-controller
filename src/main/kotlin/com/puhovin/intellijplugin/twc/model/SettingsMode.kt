package com.puhovin.intellijplugin.twc.model

import java.io.Serializable

/** Источник предпочтений для проекта: общие настройки IDE или настройки этого проекта. */
enum class SettingsMode(val value: Boolean) : Serializable {
    GLOBAL(true),
    PROJECT(false);

    companion object {
        fun fromBoolean(value: Boolean) = if (value) GLOBAL else PROJECT
    }
}