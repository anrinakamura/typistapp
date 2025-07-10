package app.typist

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import typistapp.composeapp.generated.resources.Res

@Serializable
data class Typeset(
    val character: String,
    val luminance: Double,
    val characteristic: List<Double>
)

data class PictureElement(
    val luminance: Double,
    val characteristic: List<Double>
)

class TypistArtConverter(
    // Sorted typeset list.
    private val typesetElements: List<Typeset>
) {

    fun convert(length: Int, imageBytes: ByteArray): String {
        // Separate image bytes into image elements.

        // Search the most similar typeset for each elements.

        // TODO: update
        return "typist-art".toString()
    }

    private fun pictureElements() {
        // divide an image into blocks
        // normalize
    }

    private fun searchTypesetElement() {}

    private fun closestLuminanceIndex() {}

    private fun bestMatchElement() {}

    private fun List<PictureElement>.toTypistArt(): List<Typeset> {
        return emptyList()
    }

    private fun List<Typeset>.toString(): String {
        return ""
    }

}

suspend fun readResourceFile(): String {
    return Res.readBytes("files/sample_element.json").decodeToString()
}
