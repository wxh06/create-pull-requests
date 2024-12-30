data class Config(
    val token: String,
    val branch: String = "hello-world",
    val filePath: String = "Hello.txt",
    val fileContent: String = "Hello world",
    val commitMessage: String = "Create Hello.txt",
    val pullRequestTitle: String = "Add Hello.txt",
    val pullRequestBody: String? = "Hello world",
)
