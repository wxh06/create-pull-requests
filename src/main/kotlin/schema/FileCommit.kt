package schema

import kotlinx.serialization.Serializable

/**
 * File Commit
 * @see <a href="https://docs.github.com/rest/repos/contents?apiVersion=2022-11-28">REST API endpoints for repository contents - GitHub Docs</a>
 */
@Serializable
data class FileCommit(val content: Content) {
    @Serializable
    data class Content(val name: String, val path: String, val sha: String, val size: Int, val url: String)
}
