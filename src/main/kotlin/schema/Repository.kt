package schema

import kotlinx.serialization.Serializable

/**
 * A repository on GitHub.
 * @see <a href="https://docs.github.com/rest/repos/repos?apiVersion=2022-11-28">REST API endpoints for repositories - GitHub Docs</a>
 */
@Serializable
data class Repository(
    /** Unique identifier of the repository */
    val id: Long,
    val nodeId: String,
    /** The name of the repository. */
    val name: String,
    val fullName: String,
    /** The default branch of the repository. */
    val defaultBranch: String,
)
