package team.yi.gradle.plugin;

import de.skuzzle.semantic.Version;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import team.yi.tools.semanticgitlog.VersionStrategy;
import team.yi.tools.semanticgitlog.config.GitlogSettings;
import team.yi.tools.semanticgitlog.git.GitRepo;
import team.yi.tools.semanticgitlog.model.ReleaseLog;
import team.yi.tools.semanticgitlog.render.JsonGitlogRender;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessivePublicCount"})
@Slf4j
public abstract class BaseTask extends DefaultTask {
    private Boolean skip;
    private String defaultLang;
    private Map<String, File> commitLocales;
    private Map<String, File> scopeProfiles;
    private String closeIssueActions;
    private String issueUrlTemplate;
    private String commitUrlTemplate;
    private String mentionUrlTemplate;
    private String fromRef;
    private String fromCommit;
    private String toRef;
    private String toCommit;
    private String untaggedName;
    private Boolean isUnstable;
    private VersionStrategy strategy;
    private Boolean forceNextVersion;
    private String lastVersion;
    private String tagRegex;
    private String preRelease;
    private String buildMetaData;
    private String majorTypes;
    private String minorTypes;
    private String patchTypes;
    private String preReleaseTypes;
    private String buildMetaDataTypes;
    private String hiddenTypes;
    private File jsonFile;

    @Input
    @Optional
    public Boolean getSkip() {
        return this.skip;
    }

    @Option(option = "skip", description = "Skip gitlog. Default is false.")
    public void setSkip(final Boolean skip) {
        this.skip = skip;
    }

    @Input
    @Optional
    public String getDefaultLang() {
        return this.defaultLang;
    }

    @Option(option = "defaultLang", description = "Specify the default language for localized commit messages. Default is en.")
    public void setDefaultLang(final String defaultLang) {
        this.defaultLang = defaultLang;
    }

    @Input
    @Optional
    public Map<String, File> getCommitLocales() {
        return this.commitLocales;
    }

    public void setCommitLocales(final Map<String, File> commitLocales) {
        this.commitLocales = commitLocales;
    }

    @Input
    @Optional
    public Map<String, File> getScopeProfiles() {
        return this.scopeProfiles;
    }

    public void setScopeProfiles(final Map<String, File> scopeProfiles) {
        this.scopeProfiles = scopeProfiles;
    }

    @Input
    @Optional
    public String getCloseIssueActions() {
        return this.closeIssueActions;
    }

    @Option(
        option = "closeIssueActions",
        description = "A string list that detecting close quick actions. Default is close,closes,closed,fix,fixes,fixed,resolve,resolves,resolved."
    )
    public void setCloseIssueActions(final String closeIssueActions) {
        this.closeIssueActions = closeIssueActions;
    }

    @Input
    @Optional
    public String getIssueUrlTemplate() {
        return this.issueUrlTemplate;
    }

    @Option(option = "issueUrlTemplate", description = "An url string contains placeholder :issueId to construct issue link.")
    public void setIssueUrlTemplate(final String issueUrlTemplate) {
        this.issueUrlTemplate = issueUrlTemplate;
    }

    @Input
    @Optional
    public String getCommitUrlTemplate() {
        return this.commitUrlTemplate;
    }

    @Option(option = "commitUrlTemplate", description = "An url string contains placeholder :commitId to construct commit link.")
    public void setCommitUrlTemplate(final String commitUrlTemplate) {
        this.commitUrlTemplate = commitUrlTemplate;
    }

    @Input
    @Optional
    public String getMentionUrlTemplate() {
        return this.mentionUrlTemplate;
    }

    @Option(option = "mentionUrlTemplate", description = "An url string contains placeholder :username to construct mention link.")
    public void setMentionUrlTemplate(final String mentionUrlTemplate) {
        this.mentionUrlTemplate = mentionUrlTemplate;
    }

    @Input
    @Optional
    public String getFromRef() {
        return this.fromRef;
    }

    @Option(option = "fromRef", description = "Specify the starting of git-refs. Default is null.")
    public void setFromRef(final String fromRef) {
        this.fromRef = fromRef;
    }

    @Input
    @Optional
    public String getFromCommit() {
        return this.fromCommit;
    }

    @Option(option = "fromCommit", description = "Specify the starting of git-commit. Default is null.")
    public void setFromCommit(final String fromCommit) {
        this.fromCommit = fromCommit;
    }

    @Input
    @Optional
    public String getToRef() {
        return this.toRef;
    }

    @Option(option = "toRef", description = "Specify the end of git-refs. Default is null.")
    public void setToRef(final String toRef) {
        this.toRef = toRef;
    }

    @Input
    @Optional
    public String getToCommit() {
        return this.toCommit;
    }

    @Option(option = "toCommit", description = "Specify the end of git-commit. Default is null.")
    public void setToCommit(final String toCommit) {
        this.toCommit = toCommit;
    }

    @Input
    @Optional
    public String getUntaggedName() {
        return this.untaggedName;
    }

    @Option(option = "untaggedName", description = "Specify a default title for untagged commits. Default is Unreleased.")
    public void setUntaggedName(final String untaggedName) {
        this.untaggedName = untaggedName;
    }

    @Input
    @Optional
    public Boolean getIsUnstable() {
        return this.isUnstable;
    }

    @Option(
        option = "isUnstable",
        description = "Enable unstable-period, breaking changes only increases the minor version number. Default is false."
    )
    public void setIsUnstable(final Boolean isUnstable) {
        this.isUnstable = isUnstable;
    }

    @Input
    @Optional
    public VersionStrategy getStrategy() {
        return this.strategy;
    }

    @Option(
        option = "strategy",
        description = "Release strategy. Optional values: `strict`, `slow`. Default is `strict`."
    )
    public void setStrategy(final VersionStrategy strategy) {
        this.strategy = strategy;
    }

    @Input
    @Optional
    public Boolean getForceNextVersion() {
        return this.forceNextVersion;
    }

    @Option(option = "forceNextVersion", description = "Allow force increase nextVersion when the version dose not grow. Default is true.")
    public void setForceNextVersion(final Boolean forceNextVersion) {
        this.forceNextVersion = forceNextVersion;
    }

    @Input
    @Optional
    public String getLastVersion() {
        return this.lastVersion;
    }

    @Option(
        option = "lastVersion",
        description = "Tag as version by default. This option allows you to manually specify the value of lastVersion. Default is 0.1.0."
    )
    public void setLastVersion(final String lastVersion) {
        this.lastVersion = lastVersion;
    }

    @Input
    @Optional
    public String getTagRegex() {
        return this.tagRegex;
    }

    @Option(option = "tagRegex", description = "Regex that matches all tags to be considered. Default matches all semver compliant tags.")
    public void setTagRegex(final String tagRegex) {
        this.tagRegex = tagRegex;
    }

    @Input
    @Optional
    public String getPreRelease() {
        return this.preRelease;
    }

    @Option(option = "preRelease", description = "Set the initial value of preRelease. Default is null.")
    public void setPreRelease(final String preRelease) {
        this.preRelease = preRelease;
    }

    @Input
    @Optional
    public String getBuildMetaData() {
        return this.buildMetaData;
    }

    @Option(option = "buildMetaData", description = "Set the initial value of buildMetaData. Default is null.")
    public void setBuildMetaData(final String buildMetaData) {
        this.buildMetaData = buildMetaData;
    }

    @Input
    @Optional
    public String getMajorTypes() {
        return this.majorTypes;
    }

    @Option(
        option = "majorTypes",
        description = "Increase major version when these commit types are matched. By default only when BREAKING CHANGE is discovered."
    )
    public void setMajorTypes(final String majorTypes) {
        this.majorTypes = majorTypes;
    }

    @Input
    @Optional
    public String getMinorTypes() {
        return this.minorTypes;
    }

    @Option(option = "minorTypes", description = "Increase minor version when these commit types are matched. Default is feat.")
    public void setMinorTypes(final String minorTypes) {
        this.minorTypes = minorTypes;
    }

    @Input
    @Optional
    public String getPatchTypes() {
        return this.patchTypes;
    }

    @Option(
        option = "patchTypes",
        description = "Increase patch version when these commit types are matched. Default is refactor,perf,fix,chore,revert,docs,build."
    )
    public void setPatchTypes(final String patchTypes) {
        this.patchTypes = patchTypes;
    }

    @Input
    @Optional
    public String getPreReleaseTypes() {
        return this.preReleaseTypes;
    }

    @Option(
        option = "preReleaseTypes",
        description = "Increase preRelease version when these commit types are matched. Default is null."
    )
    public void setPreReleaseTypes(final String preReleaseTypes) {
        this.preReleaseTypes = preReleaseTypes;
    }

    @Input
    @Optional
    public String getBuildMetaDataTypes() {
        return this.buildMetaDataTypes;
    }

    @Option(
        option = "buildMetaDataTypes",
        description = "Increase buildMetaData version when these commit types are matched. Default is null."
    )
    public void setBuildMetaDataTypes(final String buildMetaDataTypes) {
        this.buildMetaDataTypes = buildMetaDataTypes;
    }

    @Input
    @Optional
    public String getHiddenTypes() {
        return this.hiddenTypes;
    }

    @Option(option = "hiddenTypes", description = "These commit types are hidden in the changelog. Default is release.")
    public void setHiddenTypes(final String hiddenTypes) {
        this.hiddenTypes = hiddenTypes;
    }

    @Option(option = "jsonFile", description = "Specify the output location of the 'JSON' file. Default is null.")
    @OutputFile
    @Optional
    public File getJsonFile() {
        return this.jsonFile;
    }

    public void setJsonFile(final File jsonFile) {
        this.jsonFile = jsonFile;
    }

    protected final GitlogSettings gitlogSettings() {
        final Version lastVersion = Version.isValidVersion(this.lastVersion) ? Version.parseVersion(this.lastVersion) : null;

        return GitlogSettings.builder()
            .defaultLang(this.defaultLang)

            .commitLocales(this.commitLocales)
            .scopeProfiles(this.scopeProfiles)

            .closeIssueActions(this.closeIssueActions)
            .issueUrlTemplate(this.issueUrlTemplate)
            .commitUrlTemplate(this.commitUrlTemplate)
            .mentionUrlTemplate(this.mentionUrlTemplate)

            .fromRef(this.fromRef)
            .fromCommit(this.fromCommit)
            .toRef(this.toRef)
            .toCommit(this.toCommit)

            .untaggedName(this.untaggedName)
            .isUnstable(this.isUnstable)
            .strategy(this.strategy)
            .forceNextVersion(this.forceNextVersion)

            .lastVersion(lastVersion)
            .tagRegex(tagRegex)
            .preRelease(this.preRelease)
            .buildMetaData(this.buildMetaData)
            .majorTypes(this.majorTypes)
            .minorTypes(this.minorTypes)
            .patchTypes(this.patchTypes)
            .preReleaseTypes(this.preReleaseTypes)
            .buildMetaDataTypes(this.buildMetaDataTypes)
            .hiddenTypes(this.hiddenTypes)

            .build();
    }

    @TaskAction
    public final void execute() throws IOException {
        if (this.skip != null && this.skip) {
            log.info("Skipping changelog generation");

            return;
        }

        final File baseDir = Paths.get(System.getProperty("user.dir")).toFile();

        try (final GitRepo gitRepo = GitRepo.open(baseDir)) {
            this.execute(gitRepo);
        } catch (final Exception e) {
            log.debug(e.getMessage(), e);

            throw e;
        }
    }

    protected abstract void execute(final GitRepo gitRepo) throws IOException;

    protected void exportJson(final ReleaseLog releaseLog) throws IOException {
        if (this.jsonFile == null) return;

        final JsonGitlogRender render = new JsonGitlogRender(releaseLog, StandardCharsets.UTF_8);

        render.render(this.jsonFile);
    }
}
