import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.varabyte.kotter.foundation.anim.TextAnim
import com.varabyte.kotter.foundation.anim.text
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.collections.liveListOf
import com.varabyte.kotter.foundation.input.Completions
import com.varabyte.kotter.foundation.input.input
import com.varabyte.kotter.foundation.input.onInputEntered
import com.varabyte.kotter.foundation.input.runUntilInputEntered
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.render.aside
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import io.ktor.util.*
import schema.Repository
import kotlin.time.Duration.Companion.milliseconds

val SPINNER_ANIM = TextAnim.Template(listOf("\\", "|", "/", "-"), 250.milliseconds)
val THINKING_ANIM = TextAnim.Template(listOf("", ".", "..", "..."), 500.milliseconds)

fun main() = session {
    val config =
        ConfigLoaderBuilder.default().addResourceSource("/config.properties").build().loadConfigOrThrow<Config>()
    val githubApi = GithubRestApi(config.token)

    // List repositories
    val repositories = mutableSetOf<Repository>()
    var finished by liveVarOf(false)
    var spinnerAnim = textAnimOf(SPINNER_ANIM)
    val thinkingAnim = textAnimOf(THINKING_ANIM)
    section {
        textLine()
        if (!finished) text(spinnerAnim) else text("✓")
        text(" Fetching available repositories")
        if (!finished) text(thinkingAnim) else text("... Done!")
    }.run {
        aside {
            textLine("Available repositories:")
            textLine()
        }
        githubApi.listRepositories().collect {
            repositories.add(it)
            aside { textLine(" - ${it.fullName}") }
        }
        finished = true
    }

    // Repository selection
    var repoFullName by liveVarOf("")
    section {
        text("> ")
        input(Completions(*repositories.map { it.fullName }.toTypedArray()))
    }.runUntilInputEntered { onInputEntered { repoFullName = input } }
    val repository =
        repositories.find { it.fullName == repoFullName } ?: throw IllegalArgumentException("Repository not found")

    // Create pull request
    spinnerAnim = textAnimOf(SPINNER_ANIM)
    val tasks = liveListOf<String>()
    var url by liveVarOf<String?>(null)
    section {
        for (task in tasks.dropLast(if (url == null) 1 else 0)) textLine("✓ $task")
        if (url == null) textLine("$spinnerAnim ${tasks.last()}")
        else textLine("Successfully created pull request: $url")
    }.run {
        val owner = repository.owner.login
        val repo = repository.name

        tasks.add("Getting latest commit")
        val sha = githubApi.getReference(owner, repo, "heads/${repository.defaultBranch}").obj.sha

        tasks.add("Creating branch ${config.branch}")
        githubApi.createReference(owner, repo, "refs/heads/${config.branch}", sha)

        tasks.add("Creating file ${config.filePath}")
        githubApi.createFile(
            owner, repo, config.filePath, config.commitMessage, config.fileContent.encodeBase64(), config.branch
        )

        tasks.add("Creating pull request")
        url = githubApi.createPullRequest(
            owner, repo, config.pullRequestTitle, config.branch, repository.defaultBranch, config.pullRequestBody
        ).url

        githubApi.close()
    }
}
