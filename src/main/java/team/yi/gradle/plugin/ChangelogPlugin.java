package team.yi.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ChangelogPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project target) {
        target.getTasks().create("changelog", ChangelogTask.class);
        target.getTasks().create("derive", DeriveTask.class);
    }
}
