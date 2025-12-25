package myproject.yuikarentcos.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String, // "admin" atau "user"
    val token: String? = null
)