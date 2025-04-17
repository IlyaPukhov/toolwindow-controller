package com.puhovin.intellijplugin.twm.util;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public final class ToolWindowManagerBundle {

    @NonNls
    private static final String BUNDLE = "com.puhovin.intellijplugin.twm.bundle";
    private static Reference<ResourceBundle> ourBundle = null;

    private ToolWindowManagerBundle() {}

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;

        if (ourBundle != null) {
            bundle = ourBundle.get();
        }

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }

        return bundle;
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE) final String key, final Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }
}
