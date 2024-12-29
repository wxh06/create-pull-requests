package schema

import kotlinx.serialization.Serializable

/**
 * @see <a href="https://docs.github.com/rest/pulls/pulls?apiVersion=2022-11-28">REST API endpoints for pull requests - GitHub Docs</a>
 */
@Serializable
data class PullRequest(val url: String)
