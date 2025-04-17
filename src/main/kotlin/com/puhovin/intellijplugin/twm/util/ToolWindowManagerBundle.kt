package com.puhovin.intellijplugin.twm.util

import com.intellij.AbstractBundle
import java.util.ResourceBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

object ToolWindowManagerBundle {
    @NonNls
    private const val BUNDLE_PATH = "com.puhovin.intellijplugin.twm.bundle"
    private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE_PATH) }

    fun message(@PropertyKey(resourceBundle = BUNDLE_PATH) key: String, vararg params: Any): String {
        return AbstractBundle.message(bundle, key, *params)
    }
}
