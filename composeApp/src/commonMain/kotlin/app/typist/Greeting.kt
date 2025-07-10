package app.typist

import typistapp.composeapp.generated.resources.Res
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class Greeting {
    private val platform = getPlatform()

    suspend fun greet(): String {
        val bytes = Res.readBytes("files/sample.json")
        return bytes.decodeToString()
        // return "Hello, ${platform.name}!"
    }
}

@Serializable
data class SampleData(
    val character: String,
    val luminance: Double,
    val characteristic: List<Double>,
)

suspend fun getSampleData(): List<SampleData> {
    val jsonString = Res.readBytes("files/sample_element.json").decodeToString()
    return Json.decodeFromString<List<SampleData>>(jsonString)
}
