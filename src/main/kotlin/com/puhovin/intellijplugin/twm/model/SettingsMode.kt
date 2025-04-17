package com.puhovin.intellijplugin.twm.model

import java.io.Serializable

enum class SettingsMode(val value: Boolean) : Serializable {
    GLOBAL(true),
    PROJECT(false);

    companion object {
        fun fromBoolean(value: Boolean) = if (value) GLOBAL else PROJECT
    }
}