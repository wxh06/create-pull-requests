import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import schema.FileCommit
import schema.GitReference
import schema.PullRequest
import schema.Repository

/**
 * GitHub REST API implementation
 * @param token GitHub personal access token
 * @see <a href="https://docs.github.com/rest?apiVersion=2022-11-28">GitHub REST API documentation</a>
 */
class GithubRestApi(token: String? = null, baseUrl: String = "https://api.github.com") : GithubApi {
    @OptIn(ExperimentalSerializationApi::class)
    private val client: HttpClient = HttpClient(CIO) {
        install(Auth) {
            bearer {
                if (token != null) loadTokens {
                    BearerTokens(token, null)
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }

        defaultRequest {
            url(baseUrl)
            header("X-GitHub-Api-Version", "2022-11-28")
            contentType(ContentType.Application.Json)
        }

        expectSuccess = true
    }

    /**
     * List repositories for the authenticated user
     * @see <a href="https://docs.github.com/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-the-authenticated-user">REST API endpoints for repositories - GitHub Docs</a>
     */
    override suspend fun listRepositories(): List<Repository> = client.get("/user/repos").body()

    /**
     * Get a reference
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository without the `.git` extension. The name is not case sensitive.
     * @param ref The Git reference. For more information, see "Git References" in the Git documentation.
     * @see <a href="https://docs.github.com/rest/git/refs?apiVersion=2022-11-28#get-a-reference">REST API endpoints for Git references - GitHub Docs</a>
     */
    override suspend fun getReference(owner: String, repo: String, ref: String): GitReference =
        client.get("/repos/$owner/$repo/git/ref/$ref").body()

    /**
     * Create a reference
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository without the `.git` extension. The name is not case sensitive.
     * @param ref The name of the fully qualified reference (ie: `refs/heads/master`). If it doesn't start with 'refs' and have at least two slashes, it will be rejected.
     * @param sha The SHA1 value for this reference.
     * @see <a href="https://docs.github.com/rest/git/refs?apiVersion=2022-11-28#create-a-reference">REST API endpoints for Git references - GitHub Docs</a>
     */
    override suspend fun createReference(owner: String, repo: String, ref: String, sha: String): GitReference =
        client.post("/repos/$owner/$repo/git/refs") {
            setBody(mapOf("ref" to ref, "sha" to sha))
        }.body()

    /**
     * Create or update file contents
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository without the `.git` extension. The name is not case sensitive.
     * @param path path parameter
     * @param message The commit message.
     * @param content The new file content, using Base64 encoding.
     * @param branch The branch name. Default: the repository’s default branch.
     * @see <a href="https://docs.github.com/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents">REST API endpoints for repository contents - GitHub Docs</a>
     */
    override suspend fun createFile(
        owner: String, repo: String, path: String, message: String, content: String, branch: String?
    ): FileCommit = client.put("/repos/$owner/$repo/contents/$path") {
        setBody(mapOf("message" to message, "content" to content, "branch" to branch))
    }.body()

    /**
     * Create a pull request
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository without the `.git` extension. The name is not case sensitive.
     * @param title The title of the new pull request.
     * @param head The name of the branch where your changes are implemented. For cross-repository pull requests in the same network, namespace `head` with a user like this: `username:branch`.
     * @param base The name of the branch you want the changes pulled into. This should be an existing branch on the current repository. You cannot submit a pull request to one repository that requests a merge to a base of another repository.
     * @param body The contents of the pull request.
     * @see <a href="https://docs.github.com/rest/pulls/pulls?apiVersion=2022-11-28#create-a-pull-request">REST API endpoints for pull requests - GitHub Docs</a>
     */
    override suspend fun createPullRequest(
        owner: String, repo: String, title: String, head: String, base: String, body: String?
    ): PullRequest = client.post("/repos/$owner/$repo/pulls") {
        setBody(mapOf("title" to title, "head" to head, "base" to base, "body" to body))
    }.body()

    /**
     * Initiates the shutdown process for the `HttpClient`.
     */
    fun close() = client.close()
}
