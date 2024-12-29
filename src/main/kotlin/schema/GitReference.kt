package schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Git references within a repository
 * @see <a href="https://docs.github.com/rest/git/refs?apiVersion=2022-11-28">REST API endpoints for Git references - GitHub Docs</a>
 */
@Serializable
data class GitReference(
    val ref: String, val nodeId: String, val url: String, @SerialName("object") val obj: GitObject
) {
    @Serializable
    data class GitObject(
        /** SHA for the reference */
        val sha: String, val type: String, val url: String
    )
}
