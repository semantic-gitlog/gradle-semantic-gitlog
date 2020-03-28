package team.yi.gradle.plugin;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.gradle.api.tasks.InputFile;

import java.io.File;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class FileSet implements Serializable {
    private static final long serialVersionUID = -3010237214126366130L;

    private File template;
    private File target;

    @InputFile
    public File getTemplate() {
        return template;
    }

    public void setTemplate(final File template) {
        this.template = template;
    }

    @InputFile
    public File getTarget() {
        return target;
    }

    public void setTarget(final File target) {
        this.target = target;
    }
}
