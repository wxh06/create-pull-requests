import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.*
import kotlinx.coroutines.flow.toList

suspend fun main() {
    val config =
        ConfigLoaderBuilder.default().addResourceSource("/config.properties").build().loadConfigOrThrow<Config>()
    val githubApi = GithubRestApi(config.token)

    val repositories = githubApi.listRepositories().toList()
    println("Available repositories:")
    repositories.map { it.fullName }.forEach(::println)
    print("Enter the full name of the repository to create a pull request: ")
    val repoFullName = readln()

    val repository =
        repositories.find { it.fullName == repoFullName } ?: throw IllegalArgumentException("Repository not found")
    val owner = repository.owner.login
    val repo = repository.name

    val sha = githubApi.getReference(owner, repo, "heads/${repository.defaultBranch}").obj.sha
    githubApi.createReference(owner, repo, "refs/heads/${config.branch}", sha)
    githubApi.createFile(
        owner, repo, config.filePath, config.commitMessage, config.fileContent.encodeBase64(), config.branch
    )
    githubApi.createPullRequest(
        owner, repo, config.pullRequestTitle, config.branch, repository.defaultBranch, config.pullRequestBody
    )
    githubApi.close()
}
