import schema.FileCommit
import schema.GitReference
import schema.PullRequest
import schema.Repository

interface GithubApi {
    suspend fun listRepositories(): List<Repository>

    suspend fun getReference(owner: String, repo: String, ref: String): GitReference

    suspend fun createReference(owner: String, repo: String, ref: String, sha: String): GitReference

    suspend fun createFile(
        owner: String, repo: String, path: String, message: String, content: String, branch: String?
    ): FileCommit

    suspend fun createPullRequest(
        owner: String, repo: String, title: String, head: String, base: String, body: String?
    ): PullRequest
}
