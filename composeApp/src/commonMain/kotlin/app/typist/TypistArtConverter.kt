package app.typist

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.imageResource
import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.resized_monalisa

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

    fun convert(anImage: ImageBitmap): String {
        // val i = imageResource(resource = Res.drawable.resized_monalisa)
        // val anImage = imageBytes.decodeToImageBitmap()

        // Separate image bytes into image elements.
        // TODO: update
        val imageWidth = anImage.width;
        val imageHeight = anImage.height;

        val columns = 32
        val size = imageWidth / columns
        val lines = imageHeight / size

        val pixelMap = anImage.toPixelMap()

        for (y in 0 until lines) {
            for (x in 0 until columns) {
                for (offsetY in 0..size) {
                    for (offsetX in 0..size) {
                        val color = pixelMap[x * size + offsetX, y * size + offsetY]
                        val luminance = 0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue
                        println("luminance: $luminance")
                    }
                }
            }
        }

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

suspend fun readResourceFile(): List<Typeset> {
    val jsonString = Res.readBytes("files/sample_element.json").decodeToString()
    return Json.decodeFromString<List<Typeset>>(jsonString)
}
