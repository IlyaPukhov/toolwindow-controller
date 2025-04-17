package com.puhovin.intellijplugin.twm.model;

import java.io.Serializable;

public enum SettingsMode implements Serializable {
    GLOBAL(true),
    PROJECT(false);


    private final boolean value;

    SettingsMode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public static SettingsMode fromBoolean(boolean value) {
        return value ? GLOBAL : PROJECT;
    }
}
