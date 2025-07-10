package app.typist

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
