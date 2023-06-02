package team.yi.gradle.plugin;

import groovy.lang.Closure;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.tasks.Input;
import org.gradle.util.ConfigureUtil;
import team.yi.tools.semanticgitlog.config.GitlogSettings;
import team.yi.tools.semanticgitlog.git.GitRepo;
import team.yi.tools.semanticgitlog.model.ReleaseLog;
import team.yi.tools.semanticgitlog.render.MustacheGitlogRender;
import team.yi.tools.semanticgitlog.service.CommitLocaleService;
import team.yi.tools.semanticgitlog.service.GitlogService;
import team.yi.tools.semanticgitlog.service.ScopeProfileService;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ChangelogTask extends BaseTask {
    public static final String DEFAULT_TEMPLATE_FILE = "config/gitlog/CHANGELOG.md.mustache";
    public static final String DEFAULT_TARGET_FILE = "CHANGELOG.md";

    private Set<FileSet> fileSets;

    @Input
    public Set<FileSet> getFileSets() {
        return this.fileSets;
    }

    @SuppressWarnings("PMD.MethodArgumentCouldBeFinal")
    public void setFileSets(List<Closure<FileSet>> fileSets) {
        log.info("{}", fileSets.size());

        this.fileSets = new HashSet<>();

        for (final Closure<FileSet> closure : fileSets) {
            final FileSet fileSet = new FileSet();

            ConfigureUtil.configure(closure, fileSet);

            if (fileSet.getTemplate() == null || fileSet.getTarget() == null) continue;

            this.fileSets.add(fileSet);
        }
    }

    @SuppressWarnings("PMD.MethodArgumentCouldBeFinal")
    public void setFileSets(Set<FileSet> fileSets) {
        this.fileSets = fileSets;
    }

    @Override
    public void execute(final GitRepo gitRepo) throws IOException {
        this.saveToFile(gitRepo);
    }

    private void saveToFile(final GitRepo gitRepo) throws IOException {
        final Set<FileSet> fileSets = new HashSet<>(this.getFileSets());

        if (fileSets.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("No output set, using file " + DEFAULT_TARGET_FILE);
            }

            final File template = new File(DEFAULT_TEMPLATE_FILE);
            final File target = new File(DEFAULT_TARGET_FILE);

            fileSets.add(new FileSet(template, target));
        }

        final GitlogSettings gitlogSettings = this.gitlogSettings();

        final CommitLocaleService commitLocaleProvider = new CommitLocaleService(gitlogSettings.getDefaultLang());
        commitLocaleProvider.load(gitlogSettings.getCommitLocales());

        final GitlogService gitlogService = new GitlogService(gitlogSettings, gitRepo, commitLocaleProvider);
        final ReleaseLog releaseLog = gitlogService.generate();

        final ScopeProfileService scopeProfileService = new ScopeProfileService(gitlogSettings.getDefaultLang());
        scopeProfileService.load(gitlogSettings.getScopeProfiles());

        this.saveToFiles(releaseLog, fileSets, scopeProfileService);
        this.exportJson(releaseLog);
    }

    private void saveToFiles(final ReleaseLog releaseLog, final Set<FileSet> fileSets, final ScopeProfileService scopeProfileService) throws IOException {
        if (fileSets == null || fileSets.isEmpty()) return;

        for (final FileSet fileSet : fileSets) {
            final File target = fileSet.getTarget();
            final File template = fileSet.getTemplate();
            final MustacheGitlogRender render = new MustacheGitlogRender(releaseLog, template, scopeProfileService);

            render.render(target);
        }
    }
}
