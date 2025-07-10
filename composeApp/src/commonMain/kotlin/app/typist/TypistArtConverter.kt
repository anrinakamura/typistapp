package app.typist

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.imageResource
import typistapp.composeapp.generated.resources.Res
import typistapp.composeapp.generated.resources.resized_monalisa
import kotlin.math.abs
import kotlin.math.sqrt

const val DOUBLE_ALMOST_ZERO = 1e-12
const val NUM_OF_CANDIDATES = 16

@Serializable
data class TypesetElement(
    val character: String,
    val luminance: Double,
    val characteristic: List<Double>
)

data class PictureElement(
    val luminance: Double,
    val characteristic: List<Double>
)

class TypistArtConverter(
    // Sorted by luminance.
    private val typesetElements: List<TypesetElement>
) {

    fun convert(anImage: ImageBitmap): String {
        // val i = imageResource(resource = Res.drawable.resized_monalisa)
        // val anImage = imageBytes.decodeToImageBitmap()

        val imageWidth = anImage.width;
        val imageHeight = anImage.height;

        val columns = 32
        val size = imageWidth / columns
        val lines = imageHeight / size

        val pixelMap = anImage.toPixelMap()

        // Separate image bytes into image elements.
        val pictureElements = mutableListOf<PictureElement>()

        for (y in 0 until lines) {
            for (x in 0 until columns) {
                for (offsetY in 0..size) {

                    // val characteristic = mutableListOf<Double>()
                    val colors = mutableListOf<Color>()

                    for (offsetX in 0..size) {
                        val color = pixelMap[x * size + offsetX, y * size + offsetY]
                        colors.add(color)
                        // val luminance = 0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue
                        // println("luminance: $luminance")
                    }

                    val characteristic = colors.map { it.toLuminance() }
                    val luminance = characteristic.average()
                    pictureElements.add(PictureElement(luminance, characteristic))
                    println("picture element ($x, $y): $luminance")
                }
            }
        }

        // Normalize picture elements.
        // TODO: implement

        // Search the most similar typeset for each elements.

        // TODO: update
        return "typist-art".toString()
    }

    private fun Color.toLuminance(): Double {
        // TODO: update
        return (0.2126f * this.red + 0.7152f * this.green + 0.0722f * this.blue).toDouble()
    }

    private fun pictureElements() {
        // divide an image into blocks
        // normalize
    }

    private fun List<PictureElement>.toTypistArt(): List<TypesetElement> {
        val elements = this.map { searchTypesetElement(it) }
        return elements
    }

    private fun searchTypesetElement(pictureElement: PictureElement): TypesetElement {
        // STEP 1: find the index of the character with the most similar average luminance.
        val index = closestLuminanceIndex(pictureElement.luminance)

        // STEP 2: create a slice of candidates around that index for a more detailed search.
        val from = (index - NUM_OF_CANDIDATES / 2).coerceAtLeast(0)
        val to = (from + NUM_OF_CANDIDATES).coerceAtMost(typesetElements.size)
        val candidates = typesetElements.subList(from, to)

        // TODO: update to use default element
        if (candidates.isEmpty()) {
            return typesetElements.getOrNull(index) ?: TypesetElement("　", 0.0, emptyList())
        }

        // STEP 3: from the candidates, find the best match using pixel-by-pixel correlation.
        return bestMatchElement(pictureElement, candidates)
    }

    private fun closestLuminanceIndex(target: Double): Int {
        // TODO: fix
        val targetElement = TypesetElement("", target, emptyList())
        val index = typesetElements.binarySearch(targetElement, compareBy { it.luminance })

        return when {
            index >= 0 -> index
            // binarySearch returns -(index + 1) if the element is not found.
            index <= -typesetElements.size -> typesetElements.size - 1
            else -> {
                // val i = -(index + 1)
                // val left = typesetElements.getOrNull(index - 1)
                // val right = typesetElements.getOrNull(index + 1)
                -index - 1
            }
        }
    }

    private fun bestMatchElement(target: PictureElement, candidates: List<TypesetElement>): TypesetElement {
        var max = -1.0
        var best: TypesetElement? = null

        for (candidate in candidates) {
            val result = correlation(target.characteristic, candidate.characteristic)
            result?.let { it ->
                if (it > max) {
                    max = it
                    best = candidate
                }
            }
        }

        val default = TypesetElement("　", 0.0, emptyList())
        return best ?:default
    }

    private fun correlation(xValues: List<Double>, yValues: List<Double>): Double? {
        if (xValues.size != yValues.size || xValues.isEmpty()) {
            return null
        }

        val n = xValues.size
        val meanX = xValues.sum() / n
        val meanY = yValues.sum() / n

        var numerator = 0.0
        var denX = 0.0
        var denY = 0.0

        for (i in 0 until n) {
            val diffX = xValues[i] - meanX
            val diffY = yValues[i] - meanY
            numerator += diffX * diffY
            denX += diffX * diffX
            denY += diffY * diffY
        }

        val denominator = sqrt(denX) * sqrt(denY)
        if (abs(denominator) < DOUBLE_ALMOST_ZERO) {
            val isDenXZero = abs(denX) < DOUBLE_ALMOST_ZERO
            val isDenYZero = abs(denY) < DOUBLE_ALMOST_ZERO
            val areMeansEqual = abs(meanX - meanY) < DOUBLE_ALMOST_ZERO

            return when {
                isDenXZero && isDenYZero && areMeansEqual -> 1.0
                else -> 0.0
            }
        }

        return numerator / denominator
    }

    private fun List<TypesetElement>.toString(): String {
        return ""
    }

}

suspend fun readResourceFile(): List<TypesetElement> {
    val jsonString = Res.readBytes("files/sample_element.json").decodeToString()
    return Json.decodeFromString<List<TypesetElement>>(jsonString)
}
