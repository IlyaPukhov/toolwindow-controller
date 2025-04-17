package com.puhovin.intellijplugin.twm.initialization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupManager;
import com.puhovin.intellijplugin.twm.core.ToolWindowManagerDispatcher;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class implements the {@link ProjectActivity} interface and is responsible for
 * resetting tool windows to their default visibility settings when the project is opened.
 * It executes the reset operation after the project is fully opened, ensuring that the
 * tool window preferences are restored to their defaults.
 *
 * <p>The {@link ToolWindowManagerDispatcher} is used to initialize the default tool window preferences
 * and apply them by invoking the {@link ToolWindowManagerDispatcher#reset()} method.</p>
 *
 * <p>This activity is triggered automatically when the project is started and does not require
 * any user interaction.</p>
 *
 * @see ToolWindowManagerDispatcher
 */
public class ResetToolWindowsToDefaultsOnStartup implements ProjectActivity {

    /**
     * Executes the reset operation to restore the default visibility settings for tool windows.
     * This method is called automatically when the project is opened and the {@link StartupManager}
     * ensures that the reset happens after the project is fully initialized.
     *
     * @param project      The project that is being opened.
     * @param continuation The continuation of the coroutine for the project activity execution.
     * @return A {@link Unit} instance indicating the completion of the activity.
     */
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StartupManager.getInstance(project).runAfterOpened(() -> {
                    ToolWindowManagerDispatcher dispatcher = ToolWindowManagerDispatcher.getInstance(project);
                    dispatcher.initializeDefaultPreferences(project);
                    dispatcher.reset();
                }
        );
        return Unit.INSTANCE;
    }
}
