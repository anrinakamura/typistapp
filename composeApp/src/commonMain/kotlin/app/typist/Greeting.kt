package app.typist

import typistapp.composeapp.generated.resources.Res

class Greeting {
    private val platform = getPlatform()

    suspend fun greet(): String {
        val bytes = Res.readBytes("files/sample.json")
        return bytes.decodeToString()
        // return "Hello, ${platform.name}!"
    }
}