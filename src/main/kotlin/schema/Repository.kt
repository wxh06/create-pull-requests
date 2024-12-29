package schema

import kotlinx.serialization.Serializable

/**
 * @see <a href="https://docs.github.com/rest/repos/repos?apiVersion=2022-11-28">REST API endpoints for repositories - GitHub Docs</a>
 */
@Serializable
data class Repository(
    val id: Long, val nodeId: String, val name: String, val fullName: String, val defaultBranch: String
)
