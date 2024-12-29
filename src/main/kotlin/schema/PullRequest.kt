package schema

import kotlinx.serialization.Serializable

/**
 * Pull requests let you tell others about changes you've pushed to a repository on GitHub. Once a pull request is sent, interested parties can review the set of changes, discuss potential modifications, and even push follow-up commits if necessary.
 * @see <a href="https://docs.github.com/rest/pulls/pulls?apiVersion=2022-11-28">REST API endpoints for pull requests - GitHub Docs</a>
 */
@Serializable
data class PullRequest(val url: String)
