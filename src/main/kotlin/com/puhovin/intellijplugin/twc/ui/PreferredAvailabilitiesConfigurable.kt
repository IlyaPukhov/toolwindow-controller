package com.puhovin.intellijplugin.twc.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import com.puhovin.intellijplugin.twc.util.ToolWindowControllerBundle
import javax.swing.JComponent

/** Диалог настроек проекта: черновик сохраняется только при нажатии Apply. */
class PreferredAvailabilitiesConfigurable(private val project: Project) : Configurable {

    private var view: PreferredAvailabilitiesView? = null
    private val manager get() = project.service<ToolWindowPreferencesManager>()

    override fun getDisplayName(): String = ToolWindowControllerBundle.message("configurable.display.name")

    override fun createComponent(): JComponent =
        view ?: PreferredAvailabilitiesView(project, manager).also { view = it }

    override fun isModified(): Boolean =
        view?.let { manager.isModified(it.settingsMode, it.getCurrentViewState()) } ?: false

    override fun apply() {
        view?.let { manager.apply(it.settingsMode, it.getCurrentViewState(commitEditor = true)) }
    }

    override fun reset() {
        view?.reset()
    }

    override fun disposeUIResources() {
        view = null
    }
}
