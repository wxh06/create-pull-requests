package schema

import kotlinx.serialization.Serializable

/**
 * A GitHub user.
 * @see <a href="https://docs.github.com/rest/repos/repos?apiVersion=2022-11-28">REST API endpoints for repositories - GitHub Docs</a>
 */
@Serializable
data class SimpleUser(val login: String, val id: Long)
