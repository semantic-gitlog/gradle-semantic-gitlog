package team.yi.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangelogPluginTest {
    @Test
    public void changelog() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("team.yi.semantic-gitlog");

        assertTrue(project.getTasks().getByName("changelog") instanceof ChangelogTask, "ok");
    }
}
