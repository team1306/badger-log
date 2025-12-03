package team1306.intellijplugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class StartupBadgerLogCheck implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication();
    }
}
