package com.puhovin.intellijplugin.twm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupManager;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplyToolWindowPreferencesOnStartup implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StartupManager.getInstance(project).runAfterOpened(() ->
                ToolWindowManagerDispatcher.getInstance(project).resetToDefaultPreferences()
        );
        return Unit.INSTANCE;
    }
}
