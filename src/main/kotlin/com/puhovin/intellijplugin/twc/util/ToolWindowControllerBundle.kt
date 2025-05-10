package com.puhovin.intellijplugin.twc.util

import java.util.ResourceBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

object ToolWindowControllerBundle {
    @NonNls
    private const val BUNDLE_PATH = "com.puhovin.intellijplugin.twc.bundle"
    private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE_PATH) }

    fun message(@PropertyKey(resourceBundle = BUNDLE_PATH) key: String, vararg params: Any): String {
        val message = bundle.getString(key)
        return String.format(message, *params)
    }
}
