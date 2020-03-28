package team.yi.gradle.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.options.Option;
import team.yi.tools.semanticgitlog.config.GitlogSettings;
import team.yi.tools.semanticgitlog.git.GitRepo;
import team.yi.tools.semanticgitlog.model.ReleaseLog;
import team.yi.tools.semanticgitlog.service.CommitLocaleService;
import team.yi.tools.semanticgitlog.service.GitlogService;

import java.io.IOException;

@Slf4j
public class DeriveTask extends BaseTask {
    // ./gradlew derive -i | grep 'NEXT_VERSION:==' | sed 's/^.*NEXT_VERSION:==//g'
    private String derivedVersionMark = "NEXT_VERSION:==";

    @Input
    @Optional
    public String getDerivedVersionMark() {
        return derivedVersionMark;
    }

    @Option(
        option = "derived-version-mark",
        description = "The value will output as a prefix with the version number when semantic-gitlog:derive execute. Default is null."
    )
    public void setDerivedVersionMark(final String derivedVersionMark) {
        this.derivedVersionMark = derivedVersionMark;
    }

    @Override
    public void execute(final GitRepo gitRepo) throws IOException {
        final GitlogSettings gitlogSettings = this.gitlogSettings();

        final CommitLocaleService commitLocaleProvider = new CommitLocaleService(gitlogSettings.getDefaultLang());
        commitLocaleProvider.load(gitlogSettings.getCommitLocales());

        final GitlogService gitlogService = new GitlogService(gitlogSettings, gitRepo, commitLocaleProvider);
        final ReleaseLog releaseLog = gitlogService.generate();

        if (releaseLog == null) return;

        this.exportJson(releaseLog);

        if (releaseLog.getNextVersion() == null) return;

        if (StringUtils.isEmpty(this.derivedVersionMark)) {
            if (log.isInfoEnabled()) {
                log.info(releaseLog.getNextVersion().toString());
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info(this.derivedVersionMark + releaseLog.getNextVersion().toString());
            }
        }
    }
}
