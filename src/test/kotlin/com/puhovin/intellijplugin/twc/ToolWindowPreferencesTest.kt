package com.puhovin.intellijplugin.twc

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowBalloonShowOptions
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.testFramework.junit5.RunInEdt
import com.intellij.testFramework.junit5.RunMethodInEdt
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.TestFixtures
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.replaceService
import com.intellij.util.xmlb.XmlSerializer
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferenceApplier
import com.puhovin.intellijplugin.twc.core.ToolWindowPreferencesManager
import com.puhovin.intellijplugin.twc.initialization.ToolWindowRegistrationListener
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.AVAILABLE
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.UNAFFECTED
import com.puhovin.intellijplugin.twc.model.AvailabilityPreference.UNAVAILABLE
import com.puhovin.intellijplugin.twc.model.SettingsMode
import com.puhovin.intellijplugin.twc.model.ToolWindowPreference
import com.puhovin.intellijplugin.twc.model.ToolWindowPreferenceStore
import com.puhovin.intellijplugin.twc.settingsmanager.GlobalToolWindowManagerService
import com.puhovin.intellijplugin.twc.ui.PreferredAvailabilitiesConfigurable
import java.awt.Component
import java.awt.Container
import java.lang.reflect.Proxy
import javax.swing.JCheckBox
import javax.swing.JTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@Suppress("DEPRECATION")
@RunInEdt(allMethods = false)
@TestApplication
@TestFixtures
class ToolWindowPreferencesTest {

    private val firstProject = projectFixture()
    private val secondProject = projectFixture()
    private val disposable = Disposer.newDisposable()
    private lateinit var windows: TestToolWindowManager
    private lateinit var originalGlobal: ToolWindowPreferenceStore
    private val project get() = firstProject.get()
    private val manager get() = project.service<ToolWindowPreferencesManager>()
    private val global get() = service<GlobalToolWindowManagerService>()

    @BeforeEach
    @RunMethodInEdt
    fun setUp() {
        originalGlobal = global.state
        global.loadState(ToolWindowPreferenceStore())
        windows = TestToolWindowManager()
        project.replaceService(ToolWindowManager::class.java, windows, disposable)
    }

    @AfterEach
    @RunMethodInEdt
    fun tearDown() {
        global.loadState(originalGlobal)
        Disposer.dispose(disposable)
    }

    @Test
    @RunMethodInEdt
    fun repeatedHideIsBatchedAndDefaultRestoresOriginalAvailability() {
        val window = windows.add("Terminal", true)
        apply("Terminal", UNAVAILABLE)
        manager.applyCurrentPreferences()
        manager.applyCurrentPreferences()
        assertEquals(1, windows.queue.size)
        windows.flush()
        assertFalse(window.available)
        assertEquals(1, window.changes)
        manager.applyCurrentPreferences()
        windows.flush()
        assertEquals(1, window.changes)
        apply("Terminal", UNAFFECTED)
        windows.flush()
        assertTrue(window.available)
        assertEquals(2, window.changes)
        assertTrue(global.getPreferences().isEmpty())
    }

    @Test
    fun startupAppliesOnceWithoutBuildingSettingsUi() = kotlinx.coroutines.runBlocking {
        ApplicationManager.getApplication().invokeAndWait {
            windows.add("Terminal", true)
            global.setPreferences(mapOf("Terminal" to ToolWindowPreference("Terminal", UNAVAILABLE)))
        }
        com.puhovin.intellijplugin.twc.initialization.ApplyToolWindowsPreferencesOnStartup().execute(project)
        ApplicationManager.getApplication().invokeAndWait {
            assertEquals(1, windows.queue.size)
            windows.flush()
            assertFalse(windows.windows.getValue("Terminal").available)
            assertEquals(1, windows.windows.getValue("Terminal").changes)
        }
    }

    @Test
    fun backgroundRegistrationIsMarshalledToEdt() = kotlinx.coroutines.runBlocking {
        ApplicationManager.getApplication().invokeAndWait {
            windows.add("Late", true)
            global.setPreferences(mapOf("Late" to ToolWindowPreference("Late", UNAVAILABLE)))
        }
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC)
                .toolWindowsRegistered(listOf("Late"), windows)
        }
        ApplicationManager.getApplication().invokeAndWait {
            assertEquals(1, windows.queue.size)
            windows.queue.removeFirst().run()
            assertFalse(windows.windows.getValue("Late").available)
            assertTrue(windows.queue.isEmpty())
        }
    }

    @Test
    fun backgroundRegistrationUsesLatestSettingsAndAppliesPendingChanges() = kotlinx.coroutines.runBlocking {
        ApplicationManager.getApplication().invokeAndWait {
            windows.add("Late", true)
            windows.add("Terminal", true)
            global.setPreferences(mapOf("Late" to ToolWindowPreference("Late", UNAVAILABLE)))
        }
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC)
                .toolWindowsRegistered(listOf("Late"), windows)
        }
        ApplicationManager.getApplication().invokeAndWait {
            manager.apply(
                SettingsMode.GLOBAL, listOf(
                    ToolWindowPreference("Late", AVAILABLE), ToolWindowPreference("Terminal", UNAVAILABLE)
                )
            )
            assertEquals(2, windows.queue.size)
            windows.queue.removeFirst().run()
            assertTrue(windows.windows.getValue("Late").available)
            assertEquals(0, windows.windows.getValue("Late").changes)
            assertFalse(windows.windows.getValue("Terminal").available)
            windows.flush()
            assertEquals(1, windows.windows.getValue("Terminal").changes)
        }
    }

    @Test
    fun backgroundUnregistrationForgetsWindowOnEdt() = kotlinx.coroutines.runBlocking {
        lateinit var original: TestWindow
        ApplicationManager.getApplication().invokeAndWait {
            original = windows.add("Late", true)
            apply("Late", UNAVAILABLE)
            windows.flush()
            assertFalse(original.available)
            windows.unregisterToolWindow("Late")
        }
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC)
                .toolWindowUnregistered("Late", original.window)
        }
        ApplicationManager.getApplication().invokeAndWait {
            assertEquals(1, windows.queue.size)
            windows.flush()
            val replacement = windows.add("Late", false)
            project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC)
                .toolWindowsRegistered(listOf("Late"), windows)
            windows.flush()
            manager.restoreDefaults()
            windows.flush()
            assertFalse(replacement.available)
            assertEquals(0, replacement.changes)
        }
    }

    @Test
    fun backgroundRegistrationIsSkippedIfProjectClosesBeforeEdtCallback() = kotlinx.coroutines.runBlocking {
        var disposed = false
        lateinit var proxyManager: ToolWindowPreferencesManager
        lateinit var proxyApplier: ToolWindowPreferenceApplier
        val proxyProject =
            Proxy.newProxyInstance(Project::class.java.classLoader, arrayOf(Project::class.java)) { _, method, args ->
                when (method.name) {
                    "isDisposed" -> disposed
                    "getService" -> when (args!![0]) {
                        ToolWindowPreferencesManager::class.java -> proxyManager
                        ToolWindowPreferenceApplier::class.java -> proxyApplier
                        ToolWindowManager::class.java -> windows
                        else -> error("Unexpected service: ${args[0]}")
                    }

                    else -> error("Unexpected project call: ${method.name}")
                }
            } as Project
        proxyManager = ToolWindowPreferencesManager(proxyProject)
        proxyApplier = ToolWindowPreferenceApplier(proxyProject)
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            ToolWindowRegistrationListener(proxyProject).toolWindowsRegistered(listOf("Late"), windows)
        }
        disposed = true
        ApplicationManager.getApplication().invokeAndWait {
            assertEquals(1, windows.queue.size)
            windows.flush()
            assertTrue(windows.queue.isEmpty())
        }
    }

    @Test
    @RunMethodInEdt
    fun unconfiguredRegistrationDoesNotScheduleAvailabilityChanges() {
        val window = windows.add("Untouched", false)
        project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC)
            .toolWindowsRegistered(listOf("Untouched"), windows)
        assertTrue(windows.queue.isEmpty())
        assertFalse(window.available)
        assertEquals(0, window.changes)
    }

    @Test
    @RunMethodInEdt
    fun lastRequestWinsBeforeQueuedChangesRun() {
        val window = windows.add("Terminal", true)
        apply("Terminal", UNAVAILABLE)
        manager.restoreDefaults()
        assertEquals(1, windows.queue.size)
        windows.flush()
        assertTrue(window.available)
        assertEquals(0, window.changes)
    }

    @Test
    @RunMethodInEdt
    fun defaultAvailabilityIsIsolatedBetweenProjects() {
        val first = windows.add("Terminal", true)
        val otherWindows = TestToolWindowManager()
        val otherProject = secondProject.get()
        otherProject.replaceService(ToolWindowManager::class.java, otherWindows, disposable)
        val second = otherWindows.add("Terminal", false)
        val otherManager = otherProject.service<ToolWindowPreferencesManager>()
        apply("Terminal", UNAVAILABLE)
        windows.flush()
        otherManager.apply(SettingsMode.GLOBAL, listOf(ToolWindowPreference("Terminal", AVAILABLE)))
        otherWindows.flush()
        manager.restoreDefaults()
        windows.flush()
        otherManager.applyCurrentPreferences()
        otherWindows.flush()
        assertTrue(first.available)
        assertFalse(second.available)
    }

    @Test
    @RunMethodInEdt
    fun lateRegistrationUsesSavedPreferencesAndReregistrationGetsNewDefault() {
        global.setPreferences(mapOf("Late" to ToolWindowPreference("Late", UNAVAILABLE)))
        manager.applyCurrentPreferences()
        windows.flush()
        val window = windows.add("Late", true)
        project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC).toolWindowsRegistered(listOf("Late"), windows)
        windows.flush()
        assertFalse(window.available)
        project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC).toolWindowUnregistered("Late", window.window)
        val replacement = windows.add("Late", false)
        project.messageBus.syncPublisher(ToolWindowManagerListener.TOPIC).toolWindowsRegistered(listOf("Late"), windows)
        windows.flush()
        manager.restoreDefaults()
        windows.flush()
        assertFalse(replacement.available)
        assertEquals(0, replacement.changes)
    }

    @Test
    @RunMethodInEdt
    fun openingResettingAndCancellingDialogHaveNoSideEffects() {
        windows.add("Terminal", true)
        global.setPreferences(mapOf("Terminal" to ToolWindowPreference("Terminal", UNAVAILABLE)))
        val configurable = PreferredAvailabilitiesConfigurable(project)
        val component = configurable.createComponent() as Container
        configurable.reset()
        assertFalse(configurable.isModified)
        assertTrue(windows.queue.isEmpty())
        val checkbox = descendants(component).filterIsInstance<JCheckBox>().single()
        checkbox.doClick()
        assertTrue(configurable.isModified)
        assertEquals(SettingsMode.GLOBAL, manager.settingsMode)
        configurable.reset()
        assertTrue(checkbox.isSelected)
        assertFalse(configurable.isModified)
        checkbox.doClick()
        configurable.disposeUIResources()
        assertEquals(SettingsMode.GLOBAL, manager.settingsMode)
        assertEquals(UNAVAILABLE, global.getPreferences()["Terminal"]?.availabilityPreference)
        assertTrue(windows.queue.isEmpty())
    }

    @Test
    @RunMethodInEdt
    fun switchingScopeIsSavedOnApplyAndRestoresRemovedOverrides() {
        val window = windows.add("Terminal", true)
        apply("Terminal", UNAVAILABLE)
        windows.flush()
        val configurable = PreferredAvailabilitiesConfigurable(project)
        val component = configurable.createComponent() as Container
        descendants(component).filterIsInstance<JCheckBox>().single().doClick()
        configurable.apply()
        windows.flush()
        assertEquals(SettingsMode.PROJECT, manager.settingsMode)
        assertTrue(window.available)
        assertFalse(configurable.isModified)
        assertEquals(UNAVAILABLE, global.getPreferences()["Terminal"]?.availabilityPreference)
        configurable.disposeUIResources()
    }

    @Test
    @RunMethodInEdt
    fun savingVisibleRowsPreservesPreferencesForUnregisteredWindows() {
        windows.add("Terminal", true)
        global.setPreferences(mapOf("Absent" to ToolWindowPreference("Absent", UNAVAILABLE)))
        apply("Terminal", AVAILABLE)
        assertEquals(setOf("Absent", "Terminal"), global.getPreferences().keys)
        manager.restoreDefaults()
        assertTrue(global.getPreferences().isEmpty())
        windows.flush()
    }

    @Test
    @RunMethodInEdt
    fun rendererIsReusedAndDoesNotLeakWindowIconIntoPreferenceColumn() {
        windows.add("Terminal", true)
        val configurable = PreferredAvailabilitiesConfigurable(project)
        val table = descendants(configurable.createComponent() as Container).filterIsInstance<JTable>().single()
        val renderer = table.getCellRenderer(0, 0)
        assertSame(renderer, table.getCellRenderer(0, 1))
        val name = renderer.getTableCellRendererComponent(table, "Terminal", false, false, 0, 0) as javax.swing.JLabel
        assertNotNull(name.icon)
        val preference =
            renderer.getTableCellRendererComponent(table, UNAFFECTED, false, false, 0, 1) as javax.swing.JLabel
        assertNull(preference.icon)
        table.setValueAt(UNAVAILABLE, 0, 1)
        assertTrue(configurable.isModified)
        configurable.apply()
        assertFalse(configurable.isModified)
        windows.flush()
        assertFalse(windows.windows.getValue("Terminal").available)
        configurable.disposeUIResources()
    }

    @Test
    @RunMethodInEdt
    fun malformedSavedEntriesAreIgnoredAndXmlRoundTripPreservesValidPreferences() {
        val store = ToolWindowPreferenceStore(
            mutableListOf(
                ToolWindowPreference(),
                ToolWindowPreference("missing", AVAILABLE).apply { availabilityPreference = null },
                ToolWindowPreference("", UNAVAILABLE),
                ToolWindowPreference("default", UNAFFECTED),
                ToolWindowPreference("Terminal", UNAVAILABLE)
            )
        )
        assertEquals(setOf("Terminal"), store.getPreferences().keys)
        val roundTrip = XmlSerializer.deserialize(XmlSerializer.serialize(store), ToolWindowPreferenceStore::class.java)
        assertEquals(UNAVAILABLE, roundTrip.getPreferences()["Terminal"]?.availabilityPreference)
        val exported = roundTrip.getPreferences()
        exported.getValue("Terminal").availabilityPreference = AVAILABLE
        assertEquals(UNAVAILABLE, roundTrip.getPreferences()["Terminal"]?.availabilityPreference)
    }

    @Test
    @RunMethodInEdt
    fun queuedApplicationDoesNothingAfterProjectDisposal() {
        var disposed = false
        val window = windows.add("Terminal", true)
        val proxyProject =
            Proxy.newProxyInstance(Project::class.java.classLoader, arrayOf(Project::class.java)) { _, method, _ ->
                when (method.name) {
                    "isDisposed" -> disposed
                    "getService" -> windows
                    else -> error("Unexpected project call: ${method.name}")
                }
            } as Project
        val applier = ToolWindowPreferenceApplier(proxyProject)
        applier.applyPreferences(mapOf("Terminal" to UNAVAILABLE))
        disposed = true
        windows.flush()
        assertTrue(window.available)
        assertEquals(0, window.changes)
    }

    private fun apply(id: String, preference: AvailabilityPreference) {
        manager.apply(SettingsMode.GLOBAL, listOf(ToolWindowPreference(id, preference)))
    }

    private fun descendants(root: Container): Sequence<Component> = sequence {
        for (child in root.components) {
            yield(child)
            if (child is Container) yieldAll(descendants(child))
        }
    }
}

private class TestWindow(val id: String, var available: Boolean) {

    var changes = 0
    val window = Proxy.newProxyInstance(
        ToolWindow::class.java.classLoader,
        arrayOf(ToolWindow::class.java)
    ) { proxy, method, args ->
        when (method.name) {
            "getId" -> id
            "isAvailable" -> available
            "isDisposed" -> false
            "getIcon" -> null
            "setAvailable" -> {
                available = args!![0] as Boolean; changes++; null
            }

            "hashCode" -> System.identityHashCode(proxy)
            "equals" -> proxy === args!![0]
            "toString" -> "TestWindow($id)"
            else -> error("Unexpected window call: ${method.name}")
        }
    } as ToolWindow
}

private class TestToolWindowManager : ToolWindowManager() {

    val windows = linkedMapOf<String, TestWindow>()
    val queue = ArrayDeque<Runnable>()
    fun add(id: String, available: Boolean) = TestWindow(id, available).also { windows[id] = it }
    fun flush() {
        while (queue.isNotEmpty()) queue.removeFirst().run()
    }

    override fun invokeLater(runnable: Runnable) {
        queue.addLast(runnable)
    }

    override fun getToolWindow(id: String?): ToolWindow? = windows[id]?.window

    @Suppress("OVERRIDE_DEPRECATION")
    override val toolWindowIds: Array<String> get() = windows.keys.toTypedArray()
    override val toolWindowIdSet: Set<String> get() = windows.keys
    override val focusManager: IdeFocusManager get() = error("Not used")
    override val activeToolWindowId: String? get() = null
    override val lastActiveToolWindowId: String? get() = null
    override val isEditorComponentActive: Boolean get() = true
    override fun activateEditorComponent() = Unit
    override fun canShowNotification(toolWindowId: String): Boolean = false
    override fun registerToolWindow(task: RegisterToolWindowTask): ToolWindow = error("Not used")
    override fun unregisterToolWindow(id: String) {
        windows.remove(id)
    }

    override fun notifyByBalloon(options: ToolWindowBalloonShowOptions) = Unit
    override fun getToolWindowBalloon(id: String): Balloon? = null
    override fun isMaximized(window: ToolWindow): Boolean = false
    override fun setMaximized(window: ToolWindow, maximized: Boolean) = Unit
}
